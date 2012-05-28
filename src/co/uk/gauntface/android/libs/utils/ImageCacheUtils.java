package co.uk.gauntface.android.libs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import co.uk.gauntface.android.libs.C;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public abstract class ImageCacheUtils {
	
	public static final int DEFAULT_COMPRESSION_RATE = 50;
	
	private Context mContext;
	
	public ImageCacheUtils(Context context) {
		mContext = context;
	}
	
	public String cacheBitmap(String url, String cachedImageDirectory, int compressionRate) {
		if(url == null) {
			return null;
		}
		
		File imgFile = getCachedImageFile(url, cachedImageDirectory);
		if(imgFile != null) {
			return imgFile.getAbsolutePath()+"/"+imgFile.getName();
		}
		
		// We need to download and cache the image before returning the filepath
		Bitmap bitmap = downloadImageFromUrl(url);
		String filepath = null;
		if(bitmap != null) {
			filepath = saveBitmapToCache(url, cachedImageDirectory, compressionRate, bitmap);
			bitmap.recycle();
		}
		
		return filepath;
	}
	
	public Bitmap downloadOrGetCachedBitmap(String url, String cachedImageDirectory, int compressionRate) {
		Bitmap bitmap = getCachedImage(url, cachedImageDirectory);
		if(bitmap != null) {
			return bitmap;
		}
		
		bitmap = downloadImageFromUrl(url);
		
		if(bitmap != null) {
			saveBitmapToCache(url, cachedImageDirectory, compressionRate, bitmap);
		}
		
		return bitmap;
	}
	
	public Bitmap getCachedImage(String url, String cachedImageDirectory) {
		File imgFile = getCachedImageFile(url, cachedImageDirectory);
		if(imgFile == null) {
			return null;
		}
		
		try {
			FileInputStream inputStream = new FileInputStream(imgFile);
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inPurgeable = true;
			Log.v(C.TAG, "ImageCacheUtils: getCachedImage() 1");
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			if(bitmap != null) {
				Log.v(C.TAG, "ImageCacheUtils: getCachedImage() 2 "+bitmap.isRecycled());
			}
			return bitmap;
		} catch (FileNotFoundException e) {
			Log.e(C.TAG, "ImageCacheUtils: getCachedImage() ", e);
		}
		
		return null;
	}
	
	private File getCachedImageFile(String url, String cachedImageDirectory) {
		String filename = getFilenameFromUrl(url);
		File imgFile = getFile(filename, cachedImageDirectory);
		return imgFile;
	}
	
	private File getFile(String filename, String cachedImageDirectory) {
		if(cachedImageDirectory == null) {
			Log.v(C.TAG, "ImageCacheUtils: cache directory is Null - Oops");
			return null;
		}
		
		if(mContext == null) {
			Log.v(C.TAG, "ImageCacheUtils: context is Null - Oops");
			return null;
		}
		
		File filePath = mContext.getExternalFilesDir(cachedImageDirectory);
		if(filePath != null) {
			File imgFile = new File(filePath, filename);
			if(imgFile.exists() && imgFile.canRead()) {
				return imgFile;
			}
		}
		return null;
	}
	
	private static Bitmap downloadImageFromUrl(String url) {
		HttpGet httpRequest;
		try {
			httpRequest = new HttpGet(new URL(url).toURI());
		
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = (HttpResponse) httpClient.execute(httpRequest);
			
			HttpEntity entity = httpResponse.getEntity();
			BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
			InputStream inputStream = bufferedEntity.getContent();
			
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inPurgeable = true;
			
			return BitmapFactory.decodeStream(inputStream, null, bitmapOptions);
		} catch (MalformedURLException e) {
			Log.e(C.TAG, "ImageCacheUtils downloadImageFromUrl(): ", e);
		} catch (URISyntaxException e) {
			Log.e(C.TAG, "ImageCacheUtils downloadImageFromUrl(): ", e);
		} catch (ClientProtocolException e) {
			Log.e(C.TAG, "ImageCacheUtils downloadImageFromUrl(): ", e);
		} catch (IOException e) {
			Log.e(C.TAG, "ImageCacheUtils downloadImageFromUrl(): ", e);
		}
		
		return null;
	}
	
	private String saveBitmapToCache(String url, String cachedImageDirectory, int compressionRate, Bitmap bitmap) {
		String filename = getFilenameFromUrl(url);
		if(filename == null) {
			return null;
		}
		
		File filePath = mContext.getExternalFilesDir(cachedImageDirectory);
		if(filePath == null) {
			Log.w(C.TAG, "ImageCacheUtils: I don't have a filepath to cache the images");
			return null;
		} else if(!filePath.exists() && !filePath.mkdirs()) {
			Log.w(C.TAG, "ImageCacheUtils: Attempted to make the filepaths, but it didn't work");
			return null;
		}
		
		try {
			File file = new File(filePath, filename);
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, compressionRate, out);
			out.flush();
			
			return filePath+"/"+filename;
		} catch (FileNotFoundException e) {
			Log.e(C.TAG, "ImageCacheUtils saveBitmapToCache(): ", e);
		} catch (IOException e) {
			Log.e(C.TAG, "ImageCacheUtils saveBitmapToCache(): ", e);
		}
		
		return null;
	}
	
	protected String getFilenameFromUrl(String url) {
		String[] urlSplit = url.split("/");
		if(urlSplit.length > 0) {
			return urlSplit[urlSplit.length - 1];
		}
		
		Log.w(C.TAG, "ImageCacheUtils: getFilenameFromUrl() no filename for url = "+url);
		
		return null;
	}
}
