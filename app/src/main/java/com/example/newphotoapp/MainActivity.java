package com.example.newphotoapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PHOTO_ID = 456;
    private ArrayList<Uri> photos = new ArrayList<>();
    private ArrayAdapter<Uri> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button cameraOpen = findViewById(R.id.camera_button); //кнопка для открытия камеры
        ListView imageList = findViewById(R.id.image_list); //список ссылок на изображения

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, photos);
        imageList.setAdapter(adapter); //адаптер для хранения изображений в кэше

        cameraOpen.setOnClickListener(v ->
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), PHOTO_ID)); //открытие камеры

        imageList.setOnItemClickListener((parent, view, position, id) -> {
            Uri imageUri = (Uri) parent.getItemAtPosition(position);
            Intent intent = new Intent(MainActivity.this, FullImageView.class);
            intent.putExtra("image", imageUri.toString());
            startActivity(intent); //открытие изображения в другом окне
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_ID)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data"); //фото
            Uri imageUri = saveImageStorage(photo); //сохранение ссылки на фото

            if (imageUri != null)
            {
                photos.add(imageUri); //сохранение фото
                adapter.notifyDataSetChanged(); //изменение адаптера
            }
        }
    }

    private Uri saveImageStorage(Bitmap bitmapImage) //метод для сохранения ссылки на фото
    {
        File outputDirectory = getApplicationContext().getCacheDir(); //кэш
        File imageFile = new File(outputDirectory, System.currentTimeMillis() + ".jpg"); //ссылка на изображение
        try (FileOutputStream fileOutput = new FileOutputStream(imageFile))
        {
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutput); //параметры изображения
            return Uri.fromFile(imageFile);
        }
        catch (Exception e) {
            e.printStackTrace(); //если возникла ошибка
            return null;
        }
    }
}