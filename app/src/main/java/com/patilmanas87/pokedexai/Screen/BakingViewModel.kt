package com.patilmanas87.pokedexai.Screen

import android.content.Context
import android.database.sqlite.SQLiteBlobTooBigException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.room.Room
import androidx.room.TypeConverter
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import com.patilmanas87.pokedexai.BuildConfig
import com.patilmanas87.pokedexai.R
import com.patilmanas87.pokedexai.UiState
import com.patilmanas87.pokedexai.database.AppDatabase
import com.patilmanas87.pokedexai.database.PokemonDB
import com.patilmanas87.pokedexai.database.PokemonDao
import com.patilmanas87.pokedexai.model.Body
import com.patilmanas87.pokedexai.model.PathReuest
import com.patilmanas87.pokedexai.model.Pokemon
import com.patilmanas87.pokedexai.model.SendRequest
import com.patilmanas87.pokedexai.retrofit.ApiClient
import com.patilmanas87.pokedexai.retrofit.FakeYouApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.internal.wait
import org.json.JSONObject
import retrofit2.Call
import java.util.UUID

class BakingViewModel(private val pokemonDao: PokemonDao) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val _pokemonList: MutableStateFlow<List<PokemonDB>> = MutableStateFlow(emptyList())
    val pokemonList: StateFlow<List<PokemonDB>> = _pokemonList.asStateFlow()


    private val _capturedImage: MutableStateFlow<Bitmap?> = MutableStateFlow(createDummyBitmap())
    val capturedImage: StateFlow<Bitmap?> = _capturedImage.asStateFlow()
    private val _pokemon: MutableStateFlow<Pokemon?> =
        MutableStateFlow(
            Pokemon(
                name = "Pikachu",
                type = "Electric",
                description = "Pikachu is an Electric-type Pokémon known for its electric shocks.",
                attack = "55",
                speed = "90",
                deffence = "40",
                special_deffence = "50",
                special_attack = "50"
            )
        )
    val pokemon: StateFlow<Pokemon?> = _pokemon.asStateFlow()
    private val fakeYouApi = ApiClient.retrofit.create(FakeYouApi::class.java)


    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "enter your own gemini api key"
    )

    private fun createDummyBitmap(): Bitmap {
        val width = 100
        val height = 100
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = Color.GREEN // Replace with any color or draw a shape if needed
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        return bitmap
    }

    fun playSound(context: Context, soundResId: Int) {
        val mediaPlayer = MediaPlayer.create(context, soundResId)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
    }


    fun playSoundFromUrl(context: Context, audioUrl: String) {
        val mediaPlayer = MediaPlayer().apply {
            setDataSource(context, Uri.parse(audioUrl))
            prepareAsync()
            setOnPreparedListener {
                start()
            }
            setOnCompletionListener {
                it.release()
            }
            setOnErrorListener { mp, what, extra ->
                // Handle errors
                mp.release()
                true
            }
        }
    }

    fun openPokemon(PokemonDB: PokemonDB, context: Context, navController: NavController) {
        _pokemon.value = Json.decodeFromString(PokemonDB.pokemon)
        _capturedImage.value = fromBase64String(PokemonDB.bitmap)
        playSoundFromUrl(context, PokemonDB.audioUrl)
        navController.navigate(Screens.pokemonDetails.route)

    }

    fun loadAllPokemon(navController: NavController) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val allpokemon = pokemonDao.getAllPokemon()
                _pokemonList.value = allpokemon
            } catch (e: SQLiteBlobTooBigException) {
                pokemonDao.deletePokemonById(pokemonDao.getLastPokemon())
                println(e.toString())
            }
        }
    }

    fun Bitmap.toBase64String(): String {
        val stream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun fromBase64String(base64String: String): Bitmap {
        val byteArray = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    fun setCapturedImage(bitmap: Bitmap, navController: NavController, context: Context) {
        viewModelScope.launch {
            try {
                val response = generativeModel.generateContent(content {
                    image(bitmap)
                    text("Identify the Object ,consider that object is an pokemon,give it any name which suits that object,create description about that pokemon(it should start with nameofpokemon is pokemontype pokemon  then rarelity then information about 4 lines ),define type of pokemon,and give attack,speed,deffence,special attack,special deffence your should respone like this{\"name\":\"nameofpokemon\",\"description\":\"description\",\"type\":\"type\",\"attack\":\"attack\",\"speed\":\"speed\",\"deffence\":\"deffence\",\"special_attack\":\"special_attack\",\"special_deffence\":\"special_deffence\"}")
                })
                println(response.text)
                val pokemon = Json.decodeFromString<Pokemon>(response.text.toString().trimIndent())
                _pokemon.value = pokemon
                _capturedImage.value = bitmap
                Toast.makeText(context, "Details generated", Toast.LENGTH_SHORT).show()
                val body = Body(
                    _pokemon.value!!.description,
                    "weight_dh8zry5bgkfm0z6nv3anqa9y5",
                    UUID.randomUUID().toString()
                )
                val sendData = fakeYouApi.getVoiceInference(body)
                Toast.makeText(context, "audio request", Toast.LENGTH_SHORT).show()

                if (sendData!!.success) {
                    var path: PathReuest
                    do {
                        delay(5000)
                        path = fakeYouApi.getPath(sendData.inference_job_token)
                        println("step2")
                        Toast.makeText(context, "audio is generating", Toast.LENGTH_SHORT).show()
                    } while (path.state.status != "complete_success")
                    val file = path.state.maybe_public_bucket_wav_audio_path
                    Toast.makeText(context, "audio  generated", Toast.LENGTH_SHORT).show()
                    println(file)
                    playSoundFromUrl(
                        context,
                        "https://storage.googleapis.com/vocodes-public$file"
                    )
                    println("step3")
                    delay(2000)
                    val bitmapString = bitmap.toBase64String()
                    Toast.makeText(context, "saving in database", Toast.LENGTH_SHORT).show()
                    pokemonDao.insert(
                        PokemonDB(
                            pokemon = Json.encodeToString(pokemon),
                            bitmap = bitmapString,
                            audioUrl = "https://storage.googleapis.com/vocodes-public$file"
                        )
                    )
                    navController.navigate(Screens.pokemonDetails.route) {
                        popUpTo(Screens.record.route)
                    }
                }
            } catch (e: Exception) {
                val pokemon = Pokemon(
                    "not found",
                    "This is not a pokemon u are scanning wrong object please scan other object",
                    "unknown",
                    "0",
                    "0",
                    "0",
                    "0",
                    "0"
                )
                _pokemon.value = pokemon
                _capturedImage.value = bitmap
                Toast.makeText(context, "Details generated", Toast.LENGTH_SHORT).show()
                playSound(context, R.raw.no_fount)
                println("step3")
                delay(2000)
                navController.navigate(Screens.pokemonDetails.route) {
                    popUpTo(Screens.record.route)
                }
                println(e.toString())
            }
        }
    }
}





