/*
 * Copyright (C) 2014 foolhorse.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foolhorse.demo.imagepick;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * sample of get image from camera or gallery , and alse can crop the image
 * after get it .
 * 
 * @author foolhorse.com
 * 
 */
public class MainActivity extends Activity {

	private final int REQ_CODE_IMAGE_FROM_CAMERA = 0;

	private final int REQ_CODE_IMAGE_FROM_GALLERY = 1;

	private final int REQ_CODE_IMAGE_CROP = 2;

	private String mImagePath; // the image abs path we output

	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("MARR", "requestCode:" + requestCode + "    resultCode:" + resultCode);
		switch (requestCode) {
		case REQ_CODE_IMAGE_FROM_CAMERA: {
			Log.e("MARR", "REQ_CODE_IMAGE_FROM_CAMERA:resultCode" + resultCode);
			if (Activity.RESULT_OK == resultCode) {
				// image from uri
				cropImage(Uri.fromFile(new File(mImagePath)), 240);
			}
			break;
		}
		case REQ_CODE_IMAGE_FROM_GALLERY: {
			Log.e("MARR", "REQ_CODE_IMAGE_FROM_GALLERY:resultCode:" + resultCode);
			if (Activity.RESULT_OK == resultCode) {
				// image from data
				// TODO still can not get image from uri
				cropImage(data.getData(), 240);
			}
			break;
		}
		case REQ_CODE_IMAGE_CROP: {
			if (Activity.RESULT_OK == resultCode) {
				setImageViewRes(new File(mImagePath + "crop.jpg"));
			}
			break;
		}

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	private void initView() {

		setContentView(R.layout.activity_main);

		findViewById(R.id.from_gallery_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getImageFromGallery();

			}
		});

		findViewById(R.id.from_camera_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getImageFromCamera();

			}
		});

		mImageView = (ImageView) findViewById(R.id.image);

	}

	private void getImageFromGallery() {

		mImagePath = getImagePath();

		// Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
		// The system will then launch the best application to select that kind of data for you.

		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");

		Uri uri = Uri.fromFile(new File(mImagePath));
		intent.putExtra("return-data", false); // if you want a big image, set
												// return-data false
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri); // so the uri is
														// "return-data"
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent, REQ_CODE_IMAGE_FROM_GALLERY);
		}

	}

	private void getImageFromCamera() {
		mImagePath = getImagePath();
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri uri = Uri.fromFile(new File(mImagePath));
		intent.putExtra("return-data", false); // if you want a big image, set return-data false
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri); // so the uri is "return-data"
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent, REQ_CODE_IMAGE_FROM_CAMERA);
		}
	}

	private String getImagePath() {

		String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + "ImagePickDemoDir";
		if (!createDir(dirPath)) {
			Toast.makeText(this, "目录已创建", Toast.LENGTH_LONG).show();
		}
		Time time = new Time();
		time.setToNow();
		return dirPath + File.separator + time.format2445() + ".jpg";
	}

	public boolean createDir(String dirPath) {
		if (TextUtils.isEmpty(dirPath)) {
			return false;
		}
		File dir = new File(dirPath);
		if (dir.exists() && dir.isDirectory()) {
			return true;
		}
		if (dir.exists() && !dir.isDirectory()) {
			return false;
		}
		if (!dir.exists()) {
			return dir.mkdirs();
		}
		return false;
	}

	private void cropImage(Uri uri, int size) {
		Log.e("进入剪裁的uri ：", "" + uri);

		Intent intent = new Intent("com.android.camera.action.CROP");

		intent.setDataAndType(uri, "image/*");

		intent.putExtra("crop", "true");

		// scale h and w
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// output w and h
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);

		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);

		intent.putExtra("noFaceDetection", true);

		intent.putExtra("return-data", false); // if you want a big image, set return-data false
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mImagePath + "crop.jpg"))); // so the uri is "return-data"
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

		startActivityForResult(intent, REQ_CODE_IMAGE_CROP);
	}

	private void setImageViewRes(File imageFile) {
		if (imageFile != null) {
			Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
			mImageView.setImageBitmap(bmp);
			// other way to set Image of ImageView:
			// mImageView.setImageURI(Uri.fromFile(imageFile));
		}
	}

}
