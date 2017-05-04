<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

class predictionController extends Controller
{
    //
    public function getPredict($value='')
    {
        $client = new \GuzzleHttp\Client();
        $detail = '{"Inputs":{"input1":[{"UTC":"3000","S":"0","REF":"0","DIFF":"0"}]},"GlobalParameters":{}}';
        $res = $client->request('POST','https://ussouthcentral.services.azureml.net/workspaces/ae20d353717e463cb31bb608c76c2ea4/services/79df20331bf9444fb3c6ae295c24502d/execute?api-version=2.0&details=true'
        ,[
            'headers' =>
            [
                'Authorization' => 'Bearer IyCv26ZCJkk+Wki/KhYG6RPue2HnA7ngJMMh69FZ3RYje4jDP8iChj0IgFFqghE5AUNefnbHf81gxbUpyUK3oQ==',
                'Content-Type' => 'application/json',
            ]
        ]
        , ['json' => $detail]
        // ,['form_params' => ['Inputs' => [
        //             'input1' => [
        //                 'ColumnNames' => [
        //                     "UTC",
        //                     "S",
        //                     "REF",
        //                     "DIFF"
        //                 ],
        //                 'Values' => [
        //                     "3000", "0", "0", "0"
        //                 ],
        //             ],
        //         ],
        //     ]
        // ]
    );

        echo $res->getStatusCode(); // 200
        echo $res->getBody(); // { "type": "User", ....
    }

    public function gg(){

        $client = new \GuzzleHttp\Client();
        $res = $client->request('POST', 'http://127.0.0.1/OneTwoFly/web/OneTwoFly/public/dummy',
        ['body' => 'ff']);
        echo $res->getStatusCode(); // 200
        echo $res->getBody(); // 
    }

    public function execLocalScript(){

        $output = exec("python C:\\xampp\\htdocs\\OneTwoFly\\web\\OneTwoFly\\app\\dummy.py");

        echo($output);
    }

}
