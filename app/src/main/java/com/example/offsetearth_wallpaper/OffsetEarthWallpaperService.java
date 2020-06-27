package com.example.offsetearth_wallpaper;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Bundle;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class OffsetEarthWallpaperService extends WallpaperService
{
    @Override
    public Engine onCreateEngine() {
        try {
            InputStream inputstream;
            inputstream = this.getResources().openRawResource(R.drawable.giphy);
            Movie movie = Movie.decodeStream(inputstream);
            return new OffsetEathWallpaperEngine(movie);
        } catch (Exception e) {
            Log.d("GIF", "Could not load asset");
            return null;
        }
    }

    private class OffsetEathWallpaperEngine extends Engine {
        private final int frameDuration = 20;
        private final Handler handler = new Handler();
        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                draw();
            }
        };

        private int width;
        private int height;
        private boolean visible;

        private SurfaceHolder holder;
        private Movie movie;

        public OffsetEathWallpaperEngine(Movie movie) {
            this.movie = movie;
            handler.post(drawRunner);
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.holder = surfaceHolder;
        }

        private void draw() {
            if (visible) {
                Canvas canvas = holder.lockCanvas();
                canvas.save();

                // Adjust the size and position for screen
                canvas.scale(3f, 4f);
                movie.draw(canvas, 0, 0);

                canvas.restore();
                holder.unlockCanvasAndPost(canvas);
                movie.setTime((int) (System.currentTimeMillis() % movie.duration()));

                handler.removeCallbacks(drawRunner);
                handler.postDelayed(drawRunner, frameDuration);
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                handler.post(drawRunner);
            } else {
                handler.removeCallbacks(drawRunner);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.visible = false;
            handler.removeCallbacks(drawRunner);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            this.width = width;
            this.height = height;
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            // do nothing
        }
    }
}
