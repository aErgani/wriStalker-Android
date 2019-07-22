package com.example.a405.HttpReq;

import android.os.AsyncTask;

public class HttpServer  extends AsyncTask<String, String, String> {


    @Override
    protected String doInBackground(String... strings) {
        try{
            //URL url= new URL("localhost::8080");
            //HttpURLConnection con= (HttpURLConnection) url.openConnection();
            //write additional POST data to url.getOutputStream() if you wanna use POST method

            new HttpRequestTask(
                    new HttpRequest("http://httpbin.org/post", HttpRequest.POST, "{ \"some\": \"data-æøå\" }" ),
                    new HttpRequest.Handler() {
                        @Override
                        public void response(HttpResponse response) {
                            if (response.code == 200) {
                                //textView.setText(response.body);
                            }
                        }
                    }).execute();

        }catch (Exception ex){
            // exp
        }
        return null;
    }
}
