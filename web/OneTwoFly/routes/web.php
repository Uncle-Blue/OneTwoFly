<?php

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

Route::get('/', function () {
    return view('welcome');
});

Route::get('/php', function () {
    return phpinfo();
});

Route::get('/co2', 'predictionController@view');

Route::get('/flight', 'crawlController@flight');

Route::get('/weather', 'crawlController@weather');

Route::get('/predict', 'predictionController@gg');

Route::get('/dummy', ['as' =>'predict', 'uses' => 'predictionController@execLocalScript']);
