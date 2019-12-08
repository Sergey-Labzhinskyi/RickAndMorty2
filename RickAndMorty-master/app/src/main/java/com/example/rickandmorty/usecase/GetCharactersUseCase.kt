package com.example.rickandmorty.usecase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.rickandmorty.model.CharacterList
import com.example.rickandmorty.viewmodel.CharacterListViewModel
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.operators.observable.ObservableBlockingSubscribe.subscribe
import io.reactivex.schedulers.Schedulers

class GetCharactersUseCase {

    private val TAG: String = "GetCharactersUseCase"


    private val repository = Repository()


    fun getCharacters(page: Int) : Observable<CharacterList>{
        Log.d(TAG, "getCharacters $page")
        val characterList: Observable<CharacterList>
        repository.connectionState.map { it -> it }.subscribe()
        if (true){
            characterList =  repository.getCharactersNetwork(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
               // .doOnError { t:Throwable -> Log.d(TAG, t.message ) }
        }else{
           characterList = repository.getCharactersNetwork(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }

        return characterList

//repository.connectionState
       // repository.register()

      /* return repository.getCharactersNetwork(page)
            // .map {list -> mutableLiveData.postValue(list) }
          //  .map { list -> characterListViewModel.currentList.postValue(list) }


            .subscribeOn(Schedulers.io())
           .observeOn(AndroidSchedulers.mainThread())
           .doOnError { t:Throwable -> Log.d(TAG, t.message ) }*/

    }


    }


