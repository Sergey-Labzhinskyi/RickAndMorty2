package com.example.rickandmorty.usecase

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.os.Build
import android.util.Log
import com.example.rickandmorty.data.App
import com.example.rickandmorty.data.AppDataBase
import com.example.rickandmorty.data.CharacterDao
import com.example.rickandmorty.data.ICharacterService
import com.example.rickandmorty.model.CharacterList
import com.example.rickandmorty.model.Characterr
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject


class Repository(
    private var appDataBase: AppDataBase = App.mAppDataBase,
    private var characterDao: CharacterDao = appDataBase.mCharacterDao()
) {

    private val TAG: String = "Repository"


/*    private val _connectionState = PublishSubject.create<Boolean>()
    val connectionState: Observable<Boolean> get() = _connectionState

    init {
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if(checkConnected(context)) {
                    _connectionState.onNext(true)
                } else {
                    _connectionState.onNext(false)
                }
            }
        }
        App.instance.registerReceiver(broadcastReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
    }*/


    private val _connectionState = PublishSubject.create<Boolean>()
    val connectionState: Observable<Boolean> get() = _connectionState

    init {
    Log.d(TAG,"init")

   var networkRequest = NetworkRequest.Builder ()
        .addTransportType (NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType( NetworkCapabilities.TRANSPORT_CELLULAR)
        .build ()

     var networkCallback = object: ConnectivityManager.NetworkCallback () {
        override fun onLost(network: Network?) {
            Log.d(TAG,"networkcallback called from onLost")
            //record wi-fi disconnect event

            _connectionState.onNext(true)
        }
        override fun onUnavailable() {
            Log.d(TAG,"networkcallback called from onUnvailable")
        }
        override fun onLosing(network: Network?, maxMsToLive: Int) {
            Log.d(TAG,"networkcallback called from onLosing")
        }
        override fun onAvailable(network: Network?) {
            Log.d(TAG,"NetworkCallback network called from onAvailable ")
            //record wi-fi connect event
            _connectionState.onNext(false)
        }
    }
    val connectivityManager =
        App.instance.applicationContext.getSystemService (Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

}







    @TargetApi(Build.VERSION_CODES.M)
    fun checkConnected(context: Context?): Boolean {
       /* val connectivityManager =
            App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        var  v = capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        Log.d(TAG, v.toString())
        return v*/
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        return isConnected
    }


   /* fun getCharactersDB(): Observable<CharacterList> {
        return characterDao.getAllCharacter()
            .toObservable()
    }*/
    fun getCharactersDB(): Observable<List<Characterr>> {
        return characterDao.getAllCharacter()
            .toObservable()
    }
    fun saveCharacters(list: List<Characterr>) : Completable {
      return  characterDao.insert(list)


    }


    private val retrofit =
        ICharacterService.getRetrofit().create(ICharacterService::class.java)

    fun getCharactersNetwork(page: Int): Observable<CharacterList> {
        Log.d(TAG, "getCharactersNetwork $page")
        return retrofit
            .getCharacter(page)
          //  .subscribeOn(Schedulers.io())
          //  .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                it
            }
            .doOnError { t:Throwable -> Log.d(TAG, t.message )}
            .toObservable()

    }

}