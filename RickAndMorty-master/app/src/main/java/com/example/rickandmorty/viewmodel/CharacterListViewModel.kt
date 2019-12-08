package com.example.rickandmorty.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rickandmorty.model.CharacterList
import com.example.rickandmorty.model.Characterr
import com.example.rickandmorty.usecase.GetCharactersUseCase
import com.example.rickandmorty.usecase.Repository
import io.reactivex.disposables.Disposable

class CharacterListViewModel : ViewModel() {

   // private var getCharactersUseCase: GetCharactersUseCase = GetCharactersUseCase()
    private  var getCharactersUseCase: GetCharactersUseCase? = null

    private val TAG: String = "CharacterListViewModel"

    lateinit  var d: Disposable
    private var pageNext: Int = 1





    val currentList: LiveData<List<Characterr>> get() = _currentList
    private val _currentList: MutableLiveData<List<Characterr>> = object : MutableLiveData<List<Characterr>>() {
        override fun onActive() {
                getCharactersUseCase = GetCharactersUseCase()

                d = getCharactersUseCase?.getCharacters(pageNext)?.subscribe { value = it.results
                    Log.d(TAG, it.info.toString() )
                    pageNext = it.info.next!!.replace("\\D+".toRegex(),"").toInt() }!!
               Log.d(TAG, "onActive")




        }

        override fun onInactive() {
            super.onInactive()

        }

    }
    val currentTime: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    @SuppressLint("CheckResult")
    fun moreData(){

    val onnext = { characters: CharacterList ->
        _currentList.value = characters.results
        pageNext = characters.info.next!!.replace("\\D+".toRegex(),"").toInt()

    }
    val onerror = {t: Throwable -> f(t)

    }

    getCharactersUseCase?.getCharacters(pageNext)?.subscribe(onnext, onerror)




    }

    fun f(t: Throwable){
        Log.d(TAG, "f = ${t.message}")
    }


    override fun onCleared() {
        super.onCleared()
        d.dispose()
    }
}