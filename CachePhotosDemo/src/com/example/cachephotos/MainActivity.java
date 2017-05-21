package com.example.cachephotos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.zxl.util.FileSize;
import com.zxl.util.MD5;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private String photosUrl = "http://07.imgmini.eastday.com/mobile/20160908/20160908104951_8dd5265e95aaea073fec61a28498b28f_1_mwpm_03200403.jpeg";
	//private String photosUrl2 = "http://09.imgmini.eastday.com/mobile/20160910/20160910084858_b29cbe1c8da445d0c86e8cb7c909e258_1_mwpm_03200403.jpeg";
	private String photosUrl2 = "http://sjbz.fd.zol-img.com.cn/t_s750x1334c/g5/M00/08/0A/ChMkJlhOPOSID8IiAAOAMW4LWcQAAYfLwIrRKwAA4BJ873.jpg";
	private Button btn_cachePhotos;
	private ImageView img_cache;
	private ImageView img_cache2;
	private TextView tv_cache;
	
	private String TAG = "����ͼƬ����";

	private File lochalCache = null;//�����ļ���
	private Uri photosUri;//����ͼƬ��Uri
	private Uri photosUri2;
	private String lochalPath;//����·��
	private String size;//����Ĵ�С
	
	private Handler mHandler = new Handler();
	Thread task = new Thread(new Runnable() {
		public void run() {
			try {
				tv_cache.setText("����Ŀ¼��"+lochalPath+"\n�����С��"+size);
				img_cache.setImageURI(photosUri);
				img_cache2.setImageURI(photosUri2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_photos);
		init();
		// ��������Ŀ¼��ϵͳһ���о͵ô�������Ŀ¼��
		//lochalCache = new File(Environment.getExternalStorageDirectory(),"CachePhotos");
		lochalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CachePhotos/";
		Log.i(TAG, "�����ļ��У�" + lochalPath);
		
		lochalCache = new File(lochalPath);
		if (!lochalCache.exists()) {
			lochalCache.mkdirs();
		}
		new Thread() {
			public void run() {
				try {
					photosUri = getImage(photosUrl, lochalCache);
					photosUri2 = getImage(photosUrl2, lochalCache);
					size = FileSize.getAutoFileOrFilesSize(lochalPath);
				} catch (Exception e) {
					e.printStackTrace();
				}
				mHandler.post(task);
			};
		}.start();

	}

	private void init() {
		tv_cache = (TextView) findViewById(R.id.tv_cache);
		img_cache = (ImageView) findViewById(R.id.img_cache);
		img_cache2 = (ImageView) findViewById(R.id.img_cache2);
		btn_cachePhotos = (Button) findViewById(R.id.btn_cache);
		btn_cachePhotos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ��ջ���
				File[] files = lochalCache.listFiles();
				for (File file : files) {
					file.delete();
				}
				lochalCache.delete();
				tv_cache.setText("�����С��0KB");
			}
		});
	}

	/**
	 * ��ȡ����ͼƬ,���ͼƬ�����ڻ����У��ͷ��ظ�ͼƬ������������м��ظ�ͼƬ����������
	 * @param urlͼƬ·��
	 * @return
	 */
	public Uri getImage(String url, File cacheDir) throws Exception {
		// path ->MD5 ->32�ַ���.jpg
		File localFile = new File(cacheDir, MD5.getMD5(url)
				+ url.substring(url.lastIndexOf(".")));
		if (localFile.exists()) {
			return Uri.fromFile(localFile);
		} else {
			HttpURLConnection conn = (HttpURLConnection) new URL(url)
					.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() == 200) {
				FileOutputStream outStream = new FileOutputStream(localFile);
				InputStream inputStream = conn.getInputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = inputStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				inputStream.close();
				outStream.close();
				return Uri.fromFile(localFile);
			}
		}
		return null;
	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		// ��ջ���
//		File[] files = lochalCache.listFiles();
//		for (File file : files) {
//			file.delete();
//		}
//		lochalCache.delete();
//	}

	/**
	 * ʹ��AsyncTask�첽����ͼƬ
	 * @author Administrator
	 */
	//�����ڲ���δ�����ã�ֻ��ѧϰ
	private final class AsyncImageTask extends AsyncTask<String, Integer, Uri> {
		private ImageView imageView;

		public AsyncImageTask(ImageView imageView) {
			this.imageView = imageView;
		}

		protected Uri doInBackground(String... params) {// ���߳���ִ�е�
			try {
				return getImage(params[0], lochalCache);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Uri result) {// ���������߳�
			if (result != null && imageView != null)
				imageView.setImageURI(result);
		}
	}
}
