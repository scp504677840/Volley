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
 * Volley初探
 * 
 * 1.JSON、图片(异步请求)
 * 
 * 2.网络请求的排序
 * 
 * 3.网络请求的优先级处理
 * 
 * 4.缓存
 * 
 * 5.多级别的取消请求
 * 
 * 6.与Activity生命周期联动
 * 
 * @author y1笑而过
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

	// 获取JSON字符串
	public void getJsonVolley() {
		RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);// 请求队列
		/**
		 * method:请求方式 url:请求地址 listener:正确监听事件 errorListener:错误监听事件
		 */
		// JsonObject请求
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.GET, JsonDataUrl,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject jsonObject) {
						System.out.println("请求成功:" + jsonObject);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						System.out.println("请求失败:" + error);
					}
				});
		// JsonArray请求
		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
				Request.Method.POST, JsonDataUrl, new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray jsonArray) {
						System.out.println("请求成功:" + jsonArray);

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						System.out.println("请求失败:" + error);
					}
				});
		requestQueue.add(jsonArrayRequest);
	}

	// 异步加载图片
	@SuppressWarnings("static-access")
	public void loadImage() {
		RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
		final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(
				20);
		ImageCache imageCache = new ImageCache() {

			// 放置图片资源
			@Override
			public void putBitmap(String key, Bitmap value) {
				lruCache.put(key, value);// 放入缓存中
			}

			// 获取图片资源
			@Override
			public Bitmap getBitmap(String key) {
				return lruCache.get(key);
			}
		};
		ImageLoader imageLoader = new ImageLoader(requestQueue, imageCache);
		/**
		 * view:ImageView对象 defaultImageResId:没有加载时默认的图片
		 * errorImageResId:加载错误显示的图片
		 */
		ImageListener imageListener = imageLoader.getImageListener(imageView,
				R.drawable.ic_launcher, R.drawable.ic_launcher);
		/**
		 * requestUrl:请求地址 listener:ImageListener监听
		 */
		imageLoader.get(ImgUrl, imageListener);
	}

	// NetworkImageView的使用
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

	// GET请求
	public void volleyGet() {
		StringRequest request = new StringRequest(Method.GET, JsonDataUrl,
				new Listener<String>() {

					@Override
					public void onResponse(String str) {
						System.out.println("请求成功:" + str);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError str) {
						System.out.println("请求失败:" + str);
					}
				});
		// 设置Tag的意义在于当我们想取消这个请求的时候，可以通过这个Tag来取消
		request.setTag("getTag");
		MyApplication.getHttpQueue().add(request);
	}

	// POST请求
	public void volleyPost() {
		StringRequest request = new StringRequest(Method.POST, JsonDataUrl,
				new Listener<String>() {

					@Override
					public void onResponse(String str) {
						System.out.println("请求成功:" + str);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						System.out.println("请求失败:" + error);
					}
				});
		request.setTag("postTag");
		MyApplication.getHttpQueue().add(request);
	}

	/**
	 * 与Activity生命周期联动，防止无限请求，用户体验极差
	 */
	@Override
	protected void onStop() {
		super.onStop();
		// 获取请求队列移除请求
		MyApplication.getHttpQueue().cancelAll("getTag");
		MyApplication.getHttpQueue().cancelAll("postTag");
	}

}
