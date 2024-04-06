package com.example.tareano6

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.tareano6.databinding.ActivityMain1Binding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//85c15f8c3c473e96c91a16ead4da9c5c
class MainActivity1 : AppCompatActivity() {
    private val binding: ActivityMain1Binding by lazy{
        ActivityMain1Binding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Zacapa")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }


        })
    }

    private fun fetchWeatherData(nameCity:String){
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(nameCity, "85c15f8c3c473e96c91a16ead4da9c5c", "metric" )
        response.enqueue(object : retrofit2.Callback<AppClima>{
            override fun onResponse(call: Call<AppClima>, response: Response<AppClima>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min

                    binding.temp.text= "$temperature ºC"
                    binding.clima.text = condition
                    binding.tempMax.text = "Temp Max : $maxTemp ºC"
                    binding.tempMin.text = "Temp Min : $minTemp ºC"
                    binding.humedad.text = "$humidity %"
                    binding.viento.text = "$windSpeed m/s"
                    binding.amanecer.text = "${time(sunRise)}"
                    binding.atardecer.text = "${time(sunSet)}"
                    binding.presion.text = "$seaLevel hPa"
                    binding.condiciones.text= condition
                    binding.dia.text = dayName(System.currentTimeMillis())
                        binding.fecha.text = date()
                        binding.nameCity.text ="$nameCity"

                    changeImagsaccordingtoWeatherCondition(condition)
                }
            }
            override fun onFailure(call: Call<AppClima>, t: Throwable) {
                    TODO("Not yet implemented")
            }
        })
    }

    private fun changeImagsaccordingtoWeatherCondition(conditions : String) {
        when(conditions){
            "Cielo Despejado", "Soleado", "Despejado" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Parcialmente Nublado", "Nubes", "Nublado", "Neblina", "Neblinoso" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Lluvia Ligera", "Llovizna", "Lluvia Moderada", "Lluvia Intensa", "Lluvia Pesada" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Nieve Ligera", "Nieve Moderada", "Nevando Fuerte", "Tormenta de Nieve" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timestamp:Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*100)))
    }

    fun dayName(timestamp:Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}

