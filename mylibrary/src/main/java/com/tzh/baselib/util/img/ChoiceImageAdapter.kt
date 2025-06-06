package com.tzh.baselib.util.img

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.tzh.baselib.adapter.XRvBindingHolder
import com.tzh.baselib.adapter.XRvBindingPureDataAdapter
import com.tzh.baselib.util.GsonUtil
import com.tzh.baselib.util.LoadImageUtil
import com.tzh.baselib.util.toDefault
import com.tzh.baselib.R
import com.tzh.baselib.databinding.AdapterChoiceImageBinding
import com.tzh.baselib.util.general.PermissionDetectionUtil
import com.tzh.baselib.util.picture.PictureSelectorHelper
import java.io.File
import java.util.ArrayList

class ChoiceImageAdapter(val activity : AppCompatActivity, private val num : Int = 9,val isHaveVideo : Boolean = false,val isBack : Boolean = false,val isCamera : Boolean = true) : XRvBindingPureDataAdapter<ImageDTO>(R.layout.adapter_choice_image)  {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: XRvBindingHolder, position: Int, data: ImageDTO) {
        val b = holder.getBinding<AdapterChoiceImageBinding>()

        LoadImageUtil.loadImageUrl(b.ivImg,data.file?.absolutePath.toDefault(data.image),4f)

        b.data.visibility = if (data.status == 1) View.GONE else View.VISIBLE
        b.tvNumber.text = if(isHaveVideo) "照片或视频" else "最多上传" + num + "张"
        b.llImgChoose.visibility = if (data.status == 1) View.VISIBLE else View.GONE
        b.rlDel.setOnClickListener { remove(position) }

        b.llImgChoose.setOnClickListener {
            PermissionDetectionUtil.getPermission(activity,object : PermissionDetectionUtil.DetectionListener{
                override fun ok() {
                    if (listData.size >= num) {
                        if (listData[num - 1].status == 1) {
                            if(isHaveVideo){
                                PictureSelectorHelper.onPictureSelector(activity, mutableListOf(),num - listData.size + 1,
                                    object : OnResultCallbackListener<LocalMedia> {

                                        override fun onResult(result: ArrayList<LocalMedia>?) {
                                            addDataList(localMediaToImage(result))
                                        }

                                        override fun onCancel() {}
                                    }, SelectMimeType.ofAll(),isCamera)
                            }else{
                                PictureSelectorHelper.onPictureSelector(
                                    activity,
                                    mutableListOf(),
                                    num - listData.size + 1,
                                    object : OnResultCallbackListener<LocalMedia> {
                                        override fun onResult(result: ArrayList<LocalMedia>?) {
                                            addDataList(localMediaToImage(result))
                                        }

                                        override fun onCancel() {}
                                    }, SelectMimeType.ofImage(),isCamera)
                            }
                        }
                    } else {
                        if(isHaveVideo){
                            PictureSelectorHelper.onPictureSelector(activity, mutableListOf(),num - listData.size + 1,
                                object : OnResultCallbackListener<LocalMedia> {
                                    override fun onResult(result: ArrayList<LocalMedia>?) {
                                        addDataList(localMediaToImage(result))
                                    }

                                    override fun onCancel() {}
                                }, SelectMimeType.ofAll(),isCamera)
                        }else{
                            PictureSelectorHelper.onPictureSelector(
                                activity,mutableListOf(),
                                num - listData.size + 1,
                                object : OnResultCallbackListener<LocalMedia> {
                                    override fun onResult(result: ArrayList<LocalMedia>?) {
                                        addDataList(localMediaToImage(result))
                                    }

                                    override fun onCancel() {}
                                }, SelectMimeType.ofImage(),isCamera)
                        }
                    }
                }

                override fun cancel() {
                    mListener?.getPermission()
                }
            },isBack,isCamera)
        }
    }

    var mListener : ImageChangeListener?= null
    fun setListener(listener : ImageChangeListener){
        mListener = listener
    }

    fun getList(): MutableList<ImageDTO> {
        return listData
    }

    fun remove(position: Int) {
        listData.removeAt(position)
        selectImage.removeAt(position)
        initView()
        mListener?.change()
    }

    fun removeAll() {
        listData.clear()
        selectImage.clear()
        initView()
        mListener?.change()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun initView() {
        if (listData.size < num) {
            if (listData.size > 0) {
                if(listData[0].type == 2){

                }else if (listData[listData.size - 1].status != 1) {
                    listData.add(ImageDTO(null, 1))
                }
            } else {
                listData.add(ImageDTO(null, 1))
            }
        }
        Log.e("mList====", GsonUtil.GsonString(listData))
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setDataList(list: List<ImageDTO>) {
        this.listData.clear()
        selectImage.clear()
        selectImage.addAll(list)
        this.listData.addAll(list)
        initView()
        notifyDataSetChanged()
    }

    var selectImage : MutableList<ImageDTO> = mutableListOf()
    fun addDataList(list: List<ImageDTO>?) {
        if (list != null) {
            if (listData.size > 0) {
                if (listData[listData.size - 1].status == 1) {
                    listData.removeAt(listData.size - 1)
                }
            }
            this.listData.addAll(list)
            initView()

            selectImage.addAll(list)
            mListener?.change()
        }
    }

    fun localMediaToImage(list : MutableList<LocalMedia>?) : MutableList<ImageDTO>{
        if (list != null) {
            val mList = mutableListOf<ImageDTO>()
            for (dto in list){
                mList.add(ImageDTO(File(dto.realPath), 0).apply {
                    type = if(dto.mimeType.contains("video")){
                        2
                    }else{
                        1
                    }
                })
            }

            return mList
        }else{
            return mutableListOf()
        }
    }

    interface ImageChangeListener{
        fun change()

        //需要获取权限
        fun getPermission()
    }
}