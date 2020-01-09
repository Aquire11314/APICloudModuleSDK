package com.apicloud.moduleDemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import zpSDK.zpSDK.zpBluetoothPrinter;

/**
 * 该类映射至Javascript中moduleDemo对象<br><br>
 * <strong>Js Example:</strong><br>
 * var module = api.require('moduleDemo');<br>
 * module.xxx();
 * @author APICloud
 *
 */
public class APIModuleDemo extends UZModule {

	/**
	 * 需要的接口是 ：
	 * 打开蓝牙设备
	 * 跳转蓝牙设置页面
	 * 获取设备地址
	 * 连接蓝牙打印机
	 * 打印等
	 * 断开蓝牙打印机
	 *
	 *
	 */
	static final int ACTIVITY_REQUEST_CODE_A = 100;
	
	private AlertDialog.Builder mAlert;
	private Vibrator mVibrator;
	private UZModuleContext mJsCallback;
	private MyTextView mMyTextView;
	private zpBluetoothPrinter zpSDK ;
	
	public APIModuleDemo(UZWebView webView) {
		super(webView);
		if (zpSDK==null)zpSDK= new zpBluetoothPrinter(context());
	}

	/**
	 * 打开蓝牙
	 *  moduleContext
	 */
	public void jsmethod_startBluetooth(UZModuleContext moduleContext){
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(enableBtIntent, 2);
	}

	/**
	 * 设置蓝牙
	 *  moduleContext
	 */
	public void jsmethod_toBluetoothSetting(UZModuleContext moduleContext){
		Intent enableBtIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
		startActivityForResult(enableBtIntent, 3);
	}

	/**
	 * 获取蓝牙连接设备地址
	 *  moduleContext
	 */
	public void jsmethod_getBondedDevices(UZModuleContext moduleContext){
		BluetoothAdapter myBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
		if (myBluetoothAdapter == null) {
			Toast.makeText(context(), "没有找到蓝牙适配器", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!myBluetoothAdapter.isEnabled()) {
			Toast.makeText(context(), "请打开蓝牙，并在设置中连接好打印机", Toast.LENGTH_SHORT).show();
			return;
		}
		final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
		JSONArray array=new JSONArray();
		for (BluetoothDevice device : pairedDevices) {
			JSONObject object=new JSONObject();
			try {
				object.put("DeviceName",device.getName());
				object.put("BDAddress", device.getAddress());
				array.put(object);
			} catch (JSONException e) {
				Toast.makeText(context(), e.getMessage(), Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}

		JSONObject ret = new JSONObject();
		try {
			ret.put("array", array);
		} catch (JSONException e) {
			Toast.makeText(context(), e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		moduleContext.success(ret,true);
	}

	/**
	 * 连接蓝牙打印机
	 *  address mac地址
	 */
	public void jsmethod_connect(final UZModuleContext moduleContext){
		String address = moduleContext.optString("address");
		zpSDK.connect(address);
		JSONObject ret = new JSONObject();
		try {
			ret.put("msg", "连接成功");
		} catch (JSONException e) {
			Toast.makeText(context(), e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		moduleContext.success(ret,true);
	}

	/**
	 * 关闭与打印机的连接
	 */
	public void jsmethod_disconnect(final UZModuleContext moduleContext){
		zpSDK.disconnect();
		JSONObject ret = new JSONObject();
		try {
			ret.put("msg", "关闭连接");
		} catch (JSONException e) {
			Toast.makeText(context(), e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		moduleContext.success(ret,true);
	}

	/**
	 * 是否连接
	 *
	 */
	public void jsmethod_isConnected(final UZModuleContext moduleContext){
		JSONObject ret = new JSONObject();
		try {
			ret.put("data", zpSDK.isConnected());
		} catch (JSONException e) {
			Toast.makeText(context(), e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		moduleContext.success(ret,true);
	}

	/**
	 * 页模式下打印页面
	 *
	 *  horizontal
	 *            0:正常打印，不旋转；1：整个页面顺时针旋转180°后，打印
	 *  skip
	 *            0：打印技术后不定位，直接停止；1：打印结束后定位到标签分割线，如果无缝隙，最大进纸30mm后停止
	 * @return 操作结果 0：发送成功；非0：请查询错误列表
	 */
	public void jsmethod_print(final UZModuleContext moduleContext){
		int horizontal = moduleContext.optInt("horizontal");
		int skip = moduleContext.optInt("skip");
		zpSDK.print(horizontal,skip);
	}

	/**
	 * 设置打印纸张大小(打印区域)的大小
	 *  pageWidth 打印区域宽度
	 *  pageHeight 打印区域高度
	 */
	public void jsmethod_pageSetup(final UZModuleContext moduleContext){
		int horizontal = moduleContext.optInt("pageWidth");
		int skip = moduleContext.optInt("pageHeight");
		zpSDK.pageSetup(horizontal,skip);
	}

	/**
	 * 文字
	 *
	 *  x
	 *            起始横坐标
	 *  y
	 *            起始纵坐标
	 *  str
	 *            字符串
	 *  fontsize
	 *            字体大小 1：16点阵；2：24点阵；3：32点阵；4：24点阵放大一倍；5：32点阵放大一倍
	 *            6：24点阵放大两倍；7：32点阵放大两倍；其他：24点阵
	 *  rotate
	 *            旋转角度 0：不旋转；1：90度；2：180°；3:270°
	 *  bold
	 *            是否粗体 0：取消；1：设置
	 *  underline
	 *            是有有下划线 false:没有；true：有
	 *  reverse
	 *            是否反白 false：不反白；true：反白
	 *
	 */
	public void jsmethod_drawText(final UZModuleContext moduleContext){
		int text_x = moduleContext.optInt("text_x");
		int text_y= moduleContext.optInt("text_y");
		String text= moduleContext.optString("text");
		int fontSize= moduleContext.optInt("fontSize");
		int rotate= moduleContext.optInt("rotate");
		int bold= moduleContext.optInt("bold");
		boolean reverse= moduleContext.optBoolean("reverse");
		boolean underline= moduleContext.optBoolean("underline");
		zpSDK.drawText(text_x,text_y,text,fontSize,rotate,bold,reverse,underline);
	}

	/**
	 * 文字
	 *
	 * 如果需要设置对齐方式，计算坐标时就需要知道文字区域的大小 以下四个参数就是文字区域的坐标
	 *  text_x	文字起始x坐标
	 *  text_y	文字起始y坐标
	 *  width		文本框宽度
	 *  height	文本框高度
	 *  text		文本内容
	 *  fontSize	字体大小
	 *  rotate	旋转度数
	 *  bold		加粗
	 *  reverse	反显
	 *  underline	下划线

	 *
	 */
	public void jsmethod_drawTextBox(final UZModuleContext moduleContext){
		int text_x = moduleContext.optInt("text_x");
		int text_y= moduleContext.optInt("text_y");
		int width= moduleContext.optInt("width");
		int height= moduleContext.optInt("height");
		String text= moduleContext.optString("text");
		int fontSize= moduleContext.optInt("fontSize");
		int rotate= moduleContext.optInt("rotate");
		int bold= moduleContext.optInt("bold");
		boolean reverse= moduleContext.optBoolean("reverse");
		boolean underline= moduleContext.optBoolean("underline");
		zpSDK.drawText(text_x,text_y,text,fontSize,rotate,bold,reverse,underline);
	}

	/**
	 * 二维码
	 *  start_x 二维码起始位置
	 *  start_y 二维码结束位置
	 *  text 二维码内容
	 *  rotate 旋转角度
	 *  ver : QrCode宽度(2-6)
	 *  lel : QrCode纠错等级(0-20)
	 */
	public void jsmethod_drawQrCode(final UZModuleContext moduleContext){
		int start_x=moduleContext.optInt("start_x");
		int start_y=moduleContext.optInt("start_y");
		String text=moduleContext.optString("text");
		int rotate=moduleContext.optInt("rotate");
		int ver=moduleContext.optInt("ver");
		int lel=moduleContext.optInt("lel");
		zpSDK.drawQrCode(start_x,start_y,text,rotate,ver,lel);
	}

	/**
	 * 页模式下绘制一维条码
	 *
	 *  x
	 *            打印的起始横坐标
	 *  y
	 *            打印的起始纵坐标
	 *  str
	 *            字符串
	 *  barcodetype
	 *            条码类型
	 *            0：CODE39；1：CODE128；2：CODE93；3：CODEBAR；4：EAN8；5：EAN13；6：UPCA
	 *            ;7:UPC-E;8:ITF
	 *  rotate
	 *            true false
	 *  barWidth
	 *            条码宽度
	 *  barHeight
	 *            条码高度
	 */
	public void jsmethod_drawBarCode(final UZModuleContext moduleContext){
		int start_x=moduleContext.optInt("start_x");
		int start_y=moduleContext.optInt("start_y");
		String text=moduleContext.optString("text");
		int type=moduleContext.optInt("type");
		boolean rotate=moduleContext.optBoolean("rotate");
		int linewidth=moduleContext.optInt("linewidth");
		int height=moduleContext.optInt("height");
		zpSDK.drawBarCode(start_x,start_y,text,type,rotate,linewidth,height);

	}

	/**
	 * 返回打印机的状态信息
	 * @return
	 */
	public void jsmethod_printerStatus(final UZModuleContext moduleContext){
		JSONObject ret = new JSONObject();
		try {
			ret.put("data", zpSDK.printerStatus());
		} catch (JSONException e) {
			Toast.makeText(context(), e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		moduleContext.success(ret,true);
	}

	/**
	 * 定位到标签
	 */
	public void jsmethod_feed(final UZModuleContext moduleContext){
		zpSDK.feed();
	}

	/**
	 * 边框
	 *  lineWidth 边框线条宽度
	 *  top_left_x 矩形框左上角x坐标
	 *  top_left_y 矩形框左上角y坐标
	 *  bottom_right_x 矩形框右下角x坐标
	 *  bottom_right_y 矩形框右下角y坐标
	 */
	public void jsmethod_drawBox(final UZModuleContext moduleContext){
		int lineWidth=moduleContext.optInt("lineWidth");
		int top_left_x=moduleContext.optInt("top_left_x");
		int top_left_y=moduleContext.optInt("top_left_y");
		int bottom_right_x=moduleContext.optInt("bottom_right_x");
		int bottom_right_y=moduleContext.optInt("bottom_right_y");
		zpSDK.drawBox(lineWidth,top_left_x,top_left_y,bottom_right_x,bottom_right_y);
	}

	/**
	 * 线条
	 *  lineWidth 线条宽度
	 *  start_x 线条起始点x坐标
	 *  start_y 线条起始点y坐标
	 *  end_x 线条结束点x坐标
	 *  end_y 线条结束点y坐标
	 *  fullline  true:实线  false: 虚线
	 */
	public void jsmethod_drawLine(final UZModuleContext moduleContext){
		int lineWidth=moduleContext.optInt("lineWidth");
		int start_x=moduleContext.optInt("start_x");
		int start_y=moduleContext.optInt("start_y");
		int end_x=moduleContext.optInt("end_x");
		int end_y=moduleContext.optInt("end_y");
		boolean fullline=moduleContext.optBoolean("fullline");
		zpSDK.drawLine(lineWidth,start_x,start_y,end_x,end_y,fullline);
	}




















	/**
	 * <strong>函数</strong><br><br>
	 * 该函数映射至Javascript中moduleDemo对象的showAlert函数<br><br>
	 * <strong>JS Example：</strong><br>
	 * moduleDemo.showAlert(argument);
	 * 
	 *  moduleContext  (Required)
	 */
	public void jsmethod_showAlert(final UZModuleContext moduleContext){

		if(null != mAlert){
			return;
		}
		String showMsg = moduleContext.optString("msg");
		mAlert = new AlertDialog.Builder(context());
		mAlert.setTitle("这是标题");
		mAlert.setMessage(showMsg);
		mAlert.setCancelable(false);
		mAlert.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				mAlert = null;
				BluetoothAdapter myBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
				final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
				JSONArray array=new JSONArray();
				for (BluetoothDevice device : pairedDevices) {
					JSONObject object=new JSONObject();
					try {
						object.put("DeviceName",device.getName());
						object.put("BDAddress", device.getAddress());
						array.put(object);
					} catch (JSONException e) {
						Toast.makeText(context(), e.getMessage(), Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}

				JSONObject ret = new JSONObject();
				try {
					ret.put("array", array);
				} catch (JSONException e) {
					Toast.makeText(context(), e.getMessage(), Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				moduleContext.success(ret,true);
			}
		});
		mAlert.show();
	}
	
	/**
	 * <strong>函数</strong><br><br>
	 * 该函数映射至Javascript中moduleDemo对象的startActivity函数<br><br>
	 * <strong>JS Example：</strong><br>
	 * moduleDemo.startActivity(argument);
	 * 
	 *  moduleContext  (Required)
	 */
	public void jsmethod_startActivity(UZModuleContext moduleContext){
		/*Intent intent = new Intent(context(), DemoActivity.class);
		intent.putExtra("appParam", moduleContext.optString("appParam"));
		startActivity(intent);*/
		/*Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(enableBtIntent, 2);*/
		Intent enableBtIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
		startActivityForResult(enableBtIntent, 3);
	}
	
	/**
	 * <strong>函数</strong><br><br>
	 * 该函数映射至Javascript中moduleDemo对象的startActivityForResult函数<br><br>
	 * <strong>JS Example：</strong><br>
	 * moduleDemo.startActivityForResult(argument);
	 * 
	 *  moduleContext  (Required)
	 */
	public void jsmethod_startActivityForResult(UZModuleContext moduleContext){
		mJsCallback = moduleContext;
		Intent intent = new Intent(context(), DemoActivity.class);
		intent.putExtra("appParam", moduleContext.optString("appParam"));
		intent.putExtra("needResult", true);
		startActivityForResult(intent, ACTIVITY_REQUEST_CODE_A);
	}
	
	/**
	 * <strong>函数</strong><br><br>
	 * 该函数映射至Javascript中moduleDemo对象的vibrate函数<br><br>
	 * <strong>JS Example：</strong><br>
	 * moduleDemo.vibrate(argument);
	 * 
	 *  moduleContext  (Required)
	 */
	public void jsmethod_vibrate(UZModuleContext moduleContext){
		try {
			if (null == mVibrator) {
				mVibrator = (Vibrator) context().getSystemService(Context.VIBRATOR_SERVICE);
			}
			mVibrator.vibrate(moduleContext.optLong("milliseconds"));
		} catch (SecurityException e) {
			Toast.makeText(context(), "no vibrate permisson declare", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * <strong>函数</strong><br><br>
	 * 该函数映射至Javascript中moduleDemo对象的stopVibrate函数<br><br>
	 * <strong>JS Example：</strong><br>
	 * moduleDemo.stopVibrate(argument);
	 * 
	 *  moduleContext  (Required)
	 */
	public void jsmethod_stopVibrate(UZModuleContext moduleContext){
		if (null != mVibrator) {
			try {
				mVibrator.cancel();
				mVibrator = null;
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * <strong>函数</strong><br><br>
	 * 该函数映射至Javascript中moduleDemo对象的addView函数<br><br>
	 * <strong>JS Example：</strong><br>
	 * moduleDemo.addView(argument);
	 * 
	 *  moduleContext  (Required)
	 */
	public void jsmethod_addView(UZModuleContext moduleContext){
		int x = moduleContext.optInt("x");
		int y = moduleContext.optInt("y");
		int w = moduleContext.optInt("w");
		int h = moduleContext.optInt("h");
		if(w == 0){
			w = ViewGroup.LayoutParams.MATCH_PARENT;
		}
		if(h == 0){
			h = ViewGroup.LayoutParams.MATCH_PARENT;
		}
		if(null == mMyTextView){
			mMyTextView = new MyTextView(context());
		}
		int FROM_TYPE = Animation.RELATIVE_TO_PARENT;
		Animation anim = new TranslateAnimation(FROM_TYPE, 1.0f, FROM_TYPE, 0.0f, FROM_TYPE, 0.0f, FROM_TYPE, 0.0f);
		anim.setDuration(500);
		mMyTextView.setAnimation(anim);
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(w, h);
		rlp.leftMargin = x;
		rlp.topMargin = y;
		insertViewToCurWindow(mMyTextView, rlp);
	}
	
	/**
	 * <strong>函数</strong><br><br>
	 * 该函数映射至Javascript中moduleDemo对象的removeView函数<br><br>
	 * <strong>JS Example：</strong><br>
	 * moduleDemo.removeView(argument);
	 * 
	 *  moduleContext  (Required)
	 */
	public void jsmethod_removeView(UZModuleContext moduleContext){
		if(null != mMyTextView){

			removeViewFromCurWindow(mMyTextView);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK && requestCode == ACTIVITY_REQUEST_CODE_A){
			String result = data.getStringExtra("result");
			if(null != result && null != mJsCallback){
				try {
					JSONObject ret = new JSONObject(result);
					mJsCallback.success(ret, true);
					mJsCallback = null;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onClean() {
		if(null != mAlert){
			mAlert = null;
		}
		if(null != mJsCallback){
			mJsCallback = null;
		}
	}

	class MyTextView extends TextView{

		public MyTextView(Context context) {
			super(context);
			setBackgroundColor(0xFFF0CFD0);
			setText("我是自定义View");
			setTextColor(0xFF000000);
			setGravity(Gravity.CENTER);
		}
	}
}
