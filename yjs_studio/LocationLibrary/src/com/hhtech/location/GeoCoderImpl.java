package com.hhtech.location;

import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.hhtech.base.AppUtils;

/**
 * 进行地理编码搜索（用地址检索坐标）、反地理编码搜索（用坐标检索地址）
 */
public class GeoCoderImpl implements
		OnGetGeoCoderResultListener {
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	BaiduMap mBaiduMap = null;
	MapView mMapView = null;
	
	public interface OnGeoCoderResult {
		void onGeoResult(boolean success, LatLng latLng);
		void onRevseGeoResult(boolean success, AddressComponent detailAddr);
	}
	OnGeoCoderResult geoResult;
	public GeoCoderImpl(OnGeoCoderResult result) {
		geoResult = result;
	}
	
	public void init() {
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
	}


	/**
	 * 具体地址到经纬度
	 * @param city  城市名，如福州
	 * @param address 具体地址，如(温泉支路58号)
	 */
	public void addr2LatLng(String city,String address) {
		mSearch.geocode(new GeoCodeOption().city(city).address(address));
	}
	
	
	/**
	 * 经纬度变成具体地址
	 * @param latitide 纬度
	 * @param longitude 经度
	 */
	public void latLng2Addr(float latitide,float longitude) {
		LatLng ptCenter = new LatLng(latitide,longitude);
		// 反Geo搜索
		mSearch.reverseGeoCode(new ReverseGeoCodeOption()
				.location(ptCenter));
	}
	

	public void fini() {
		mSearch.destroy();
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			showToast("抱歉，未能找到结果");
			geoResult.onGeoResult(false, null);
			return;
		}
		
		geoResult.onGeoResult(true, result.getLocation());
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			showToast("抱歉，未能找到结果");
			geoResult.onRevseGeoResult(false, null);
			return;
		}
		geoResult.onRevseGeoResult(true, result.getAddressDetail());
	}

	private void showToast(String text) {
		Toast.makeText(AppUtils.getAppContext(),text,Toast.LENGTH_SHORT).show();
	}

}
