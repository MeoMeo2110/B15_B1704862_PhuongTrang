package com.npt.phuongtrang.b15_b1704862_phuongtrang;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity
{
    EditText edtUrl1, edtUrl2;
    TextView tvLoadimg, tvLoadtext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtUrl1 = findViewById(R.id.nhapurl);
        edtUrl2 = findViewById(R.id.nhapurl2);
        tvLoadimg = findViewById(R.id.tvLoadimg);
        tvLoadtext = findViewById(R.id.tvLoadtext);

        String URL = "https://tse3.mm.bing.net/th?id=OIP.-sRPpGMNfmGOhw_lxYXOmQHaEK&pid=Api&P=0&w=280&h=158";
        String URL2 = "https://thuthuat.taimienphi.vn/huong-dan-cach-cai-dat-cau-hinh-va-su-dung-xampp-36056n.aspx";
        edtUrl1.setText(URL);
        edtUrl2.setText(URL2);

        tvLoadimg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new DownloadImageTask().execute(edtUrl1.getText().toString());
                edtUrl1.setText("");
            }
        });

        tvLoadtext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new DownloadTextTask().execute(edtUrl2.getText().toString());
                edtUrl2.setText("");
            }
        });

    }

    public InputStream OpenHttpConnection(String urlString) throws IOException
    {
        InputStream in = null;
        int response = -1;
        URL url = new URL(urlString);
        // Mở kết nối tới urlString
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");

        try
        {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setInstanceFollowRedirects(true);
            //Xác định phương thức kết nối là GET
            httpConn.setRequestMethod("GET");
            //Kết nối với máy chủ
            httpConn.connect();
            /* (4) Lấy đáp ứng HTTP_OK để biết kết nối đã được thiết lập hay chưa */
            response = httpConn.getResponseCode();
            Log.w("Response Code", ""+response);

/*****************************************************************
 * (5) Nếu kết nối được thiết lập thì tiến hành
 lấy đối tượng InputStream từ kết nối để lấy dữ liệu từ Server

 ******************************************************************/
            if (response == HttpURLConnection.HTTP_OK)
            {
                in = httpConn.getInputStream();
            }
        }
        catch (Exception e)
        {
            Log.e("Networking", e.getLocalizedMessage());
        }
        return in;
    }

    private Bitmap DownloadImage(String URL)
    {
        Bitmap bitmap = null;
        InputStream in = null;
        try
        {
            // Mở kết nối đến Server, phương thức đã được định nghĩa ở trên
            in = OpenHttpConnection(URL);
            if (in == null)
            {
                Log.e("Image URL", "Check connection or URL again!");
                return bitmap;
            }
            // Tải dữ liệu thông qua InputStream in
            // và giải mã vào đối tượng bitmap
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        }
        catch (IOException e)
        {
            Log.e("NetworkingActivity", e.getLocalizedMessage());
        }
        return bitmap;
    }

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
    {
        //Thực hiện tải dữ liệu
        // Khi hoàn tất, kết quả được truyền vào phương thức onPostExecute
        @Override
        protected Bitmap doInBackground(String... urls)
        {
            return DownloadImage(urls[0]);
        }
        @Override
        protected void onPostExecute(Bitmap result)
        {
            ImageView img = (ImageView) findViewById(R.id.img);
            //Hiển thị ảnh trên màn hình
            img.setImageBitmap(result);
            TextView tv = (TextView) findViewById(R.id.tvURLimg);
            if (result != null)
                tv.setText("Got image.");
            else
                tv.setText("Can't get image.");
        }
    }

    private String DownloadText(String URL)
    {
        int BUFFER_SIZE = 2000;
        InputStream in = null;
        String str = "";
        try
        {
            in = OpenHttpConnection(URL);
        }
        catch (IOException e)
        {
            Log.e("Networking", e.getLocalizedMessage());
            return str;
        }
        if (in == null)
        {
            Log.e("Text URL", "Check connection or URL again!");
            return str;
        }
        try
        {
            InputStreamReader isr = new InputStreamReader(in);
            int charRead;
            char[] inputBuffer = new char[BUFFER_SIZE];
            while ((charRead = isr.read(inputBuffer)) > 0)
            {
                //Chuyển chars thành String
                String readString = String.copyValueOf(inputBuffer, 0, charRead);
                str += readString;
                inputBuffer = new char[BUFFER_SIZE];
            }
            in.close();
        }
        catch (IOException e)
        {
            Log.e("Networking", e.getLocalizedMessage());
            return str;
        }
        return str;
    }

    public class DownloadTextTask extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... urls)
        {
            return DownloadText(urls[0]);
        }
        @Override
        protected void onPostExecute(String result)
        {
            TextView tv1 = (TextView) findViewById(R.id.tv);
            tv1.setText(result);
            TextView tv = (TextView) findViewById(R.id.tvURLtext);
            if (!result.trim().equals(""))
                tv.setText("Got text.");
            else
                tv.setText("Can't get text.");
        }
    }

}