package com.scp.volley;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.view.Window;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * Volley��̽
 * 
 * 1.JSON��ͼƬ(�첽����)
 * 
 * 2.�������������
 * 
 * 3.������������ȼ�����
 * 
 * 4.����
 * 
 * 5.�༶���ȡ������
 * 
 * 6.��Activity������������
 * 
 * @author y1Ц����
 *
 */
public class MainActivity extends Activity {
	private ImageView imageView;
	private NetworkImageView networkImageView;
	private static final String JsonDataUrl = "http://192.168.43.94:8080/Metoos/commodityAction!commodityAll.action";
	private static final String ImgUrl = "http://192.168.43.94:8080/Metoos/Icon.png";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_main);
		imageView = (ImageView) findViewById(R.id.main_img);
		networkImageView = (NetworkImageView) findViewById(R.id.main_netimg);
		getJsonVolley();
		loadImage();
		networkImage();
		volleyGet();
		volleyPost();
	}

	// ��ȡJSON�ַ���
	public void getJsonVolley() {
		RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);// �������
		/**
		 * method:����ʽ url:�����ַ listener:��ȷ�����¼� errorListener:��������¼�
		 */
		// JsonObject����
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.GET, JsonDataUrl,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject jsonObject) {
						System.out.println("����ɹ�:" + jsonObject);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						System.out.println("����ʧ��:" + error);
					}
				});
		// JsonArray����
		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
				Request.Method.POST, JsonDataUrl, new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray jsonArray) {
						System.out.println("����ɹ�:" + jsonArray);

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						System.out.println("����ʧ��:" + error);
					}
				});
		requestQueue.add(jsonArrayRequest);
	}

	// �첽����ͼƬ
	@SuppressWarnings("static-access")
	public void loadImage() {
		RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
		final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(
				20);
		ImageCache imageCache = new ImageCache() {

			// ����ͼƬ��Դ
			@Override
			public void putBitmap(String key, Bitmap value) {
				lruCache.put(key, value);// ���뻺����
			}

			// ��ȡͼƬ��Դ
			@Override
			public Bitmap getBitmap(String key) {
				return lruCache.get(key);
			}
		};
		ImageLoader imageLoader = new ImageLoader(requestQueue, imageCache);
		/**
		 * view:ImageView���� defaultImageResId:û�м���ʱĬ�ϵ�ͼƬ
		 * errorImageResId:���ش�����ʾ��ͼƬ
		 */
		ImageListener imageListener = imageLoader.getImageListener(imageView,
				R.drawable.ic_launcher, R.drawable.ic_launcher);
		/**
		 * requestUrl:�����ַ listener:ImageListener����
		 */
		imageLoader.get(ImgUrl, imageListener);
	}

	// NetworkImageView��ʹ��
	public void networkImage() {
		RequestQueue requestQueue = Volley.newRequestQueue(this);
		final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(
				20);
		ImageCache imageCache = new ImageCache() {

			@Override
			public void putBitmap(String key, Bitmap value) {
				lruCache.put(key, value);
			}

			@Override
			public Bitmap getBitmap(String key) {
				return lruCache.get(key);
			}
		};
		ImageLoader imageLoader = new ImageLoader(requestQueue, imageCache);
		networkImageView.setTag("url");
		networkImageView.setImageUrl(ImgUrl, imageLoader);

	}

	// GET����
	public void volleyGet() {
		StringRequest request = new StringRequest(Method.GET, JsonDataUrl,
				new Listener<String>() {

					@Override
					public void onResponse(String str) {
						System.out.println("����ɹ�:" + str);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError str) {
						System.out.println("����ʧ��:" + str);
					}
				});
		// ����Tag���������ڵ�������ȡ����������ʱ�򣬿���ͨ�����Tag��ȡ��
		request.setTag("getTag");
		MyApplication.getHttpQueue().add(request);
	}

	// POST����
	public void volleyPost() {
		StringRequest request = new StringRequest(Method.POST, JsonDataUrl,
				new Listener<String>() {

					@Override
					public void onResponse(String str) {
						System.out.println("����ɹ�:" + str);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						System.out.println("����ʧ��:" + error);
					}
				});
		request.setTag("postTag");
		MyApplication.getHttpQueue().add(request);
	}

	/**
	 * ��Activity����������������ֹ���������û����鼫��
	 */
	@Override
	protected void onStop() {
		super.onStop();
		// ��ȡ��������Ƴ�����
		MyApplication.getHttpQueue().cancelAll("getTag");
		MyApplication.getHttpQueue().cancelAll("postTag");
	}

}
