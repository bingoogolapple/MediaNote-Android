package cn.bingoogolapple.media.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/7 下午11:09
 * 描述:
 */
public class MediaFile implements Parcelable {
    public String name;
    public String path;
    public int size;
    public int duration;
    public String artist;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeInt(this.size);
        dest.writeInt(this.duration);
        dest.writeString(this.artist);
    }

    public MediaFile() {
    }

    protected MediaFile(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.size = in.readInt();
        this.duration = in.readInt();
        this.artist = in.readString();
    }

    public static final Parcelable.Creator<MediaFile> CREATOR = new Parcelable.Creator<MediaFile>() {
        public MediaFile createFromParcel(Parcel source) {
            return new MediaFile(source);
        }

        public MediaFile[] newArray(int size) {
            return new MediaFile[size];
        }
    };
}