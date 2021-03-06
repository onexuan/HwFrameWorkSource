package com.huawei.nb.model.trajectory;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.huawei.odmf.core.AManagedObject;
import com.huawei.odmf.model.AEntityHelper;
import java.util.Date;

public class HubTrajectoryData extends AManagedObject {
    public static final Creator<HubTrajectoryData> CREATOR = new Creator<HubTrajectoryData>() {
        public HubTrajectoryData createFromParcel(Parcel in) {
            return new HubTrajectoryData(in);
        }

        public HubTrajectoryData[] newArray(int size) {
            return new HubTrajectoryData[size];
        }
    };
    private Integer id;
    private Integer mCellID;
    private Integer mCellID1;
    private Integer mCellID2;
    private Character mCellLAC;
    private Character mCellLAC1;
    private Character mCellLAC2;
    private Integer mCellRssi;
    private Integer mCellRssi1;
    private Integer mCellRssi2;
    private Character mMCC;
    private Character mMCC1;
    private Character mMCC2;
    private Character mMNC;
    private Character mMNC1;
    private Character mMNC2;
    private Integer mReserved1;
    private String mReserved2;
    private Date mTimeStamp;

    public HubTrajectoryData(Cursor cursor) {
        Integer num = null;
        setRowId(Long.valueOf(cursor.getLong(0)));
        this.id = cursor.isNull(1) ? null : Integer.valueOf(cursor.getInt(1));
        this.mTimeStamp = cursor.isNull(2) ? null : new Date(cursor.getLong(2));
        this.mCellID = cursor.isNull(3) ? null : Integer.valueOf(cursor.getInt(3));
        this.mCellLAC = cursor.isNull(4) ? null : Character.valueOf(cursor.getString(4).charAt(0));
        this.mCellRssi = cursor.isNull(5) ? null : Integer.valueOf(cursor.getInt(5));
        this.mMCC = cursor.isNull(6) ? null : Character.valueOf(cursor.getString(6).charAt(0));
        this.mMNC = cursor.isNull(7) ? null : Character.valueOf(cursor.getString(7).charAt(0));
        this.mCellID1 = cursor.isNull(8) ? null : Integer.valueOf(cursor.getInt(8));
        this.mCellLAC1 = cursor.isNull(9) ? null : Character.valueOf(cursor.getString(9).charAt(0));
        this.mCellRssi1 = cursor.isNull(10) ? null : Integer.valueOf(cursor.getInt(10));
        this.mMCC1 = cursor.isNull(11) ? null : Character.valueOf(cursor.getString(11).charAt(0));
        this.mMNC1 = cursor.isNull(12) ? null : Character.valueOf(cursor.getString(12).charAt(0));
        this.mCellID2 = cursor.isNull(13) ? null : Integer.valueOf(cursor.getInt(13));
        this.mCellLAC2 = cursor.isNull(14) ? null : Character.valueOf(cursor.getString(14).charAt(0));
        this.mCellRssi2 = cursor.isNull(15) ? null : Integer.valueOf(cursor.getInt(15));
        this.mMCC2 = cursor.isNull(16) ? null : Character.valueOf(cursor.getString(16).charAt(0));
        this.mMNC2 = cursor.isNull(17) ? null : Character.valueOf(cursor.getString(17).charAt(0));
        if (!cursor.isNull(18)) {
            num = Integer.valueOf(cursor.getInt(18));
        }
        this.mReserved1 = num;
        this.mReserved2 = cursor.getString(19);
    }

    public HubTrajectoryData(Parcel in) {
        String str = null;
        super(in);
        if (in.readByte() == (byte) 0) {
            this.id = null;
            in.readInt();
        } else {
            this.id = Integer.valueOf(in.readInt());
        }
        this.mTimeStamp = in.readByte() == (byte) 0 ? null : new Date(in.readLong());
        this.mCellID = in.readByte() == (byte) 0 ? null : Integer.valueOf(in.readInt());
        this.mCellLAC = in.readByte() == (byte) 0 ? null : Character.valueOf(in.createCharArray()[0]);
        this.mCellRssi = in.readByte() == (byte) 0 ? null : Integer.valueOf(in.readInt());
        this.mMCC = in.readByte() == (byte) 0 ? null : Character.valueOf(in.createCharArray()[0]);
        this.mMNC = in.readByte() == (byte) 0 ? null : Character.valueOf(in.createCharArray()[0]);
        this.mCellID1 = in.readByte() == (byte) 0 ? null : Integer.valueOf(in.readInt());
        this.mCellLAC1 = in.readByte() == (byte) 0 ? null : Character.valueOf(in.createCharArray()[0]);
        this.mCellRssi1 = in.readByte() == (byte) 0 ? null : Integer.valueOf(in.readInt());
        this.mMCC1 = in.readByte() == (byte) 0 ? null : Character.valueOf(in.createCharArray()[0]);
        this.mMNC1 = in.readByte() == (byte) 0 ? null : Character.valueOf(in.createCharArray()[0]);
        this.mCellID2 = in.readByte() == (byte) 0 ? null : Integer.valueOf(in.readInt());
        this.mCellLAC2 = in.readByte() == (byte) 0 ? null : Character.valueOf(in.createCharArray()[0]);
        this.mCellRssi2 = in.readByte() == (byte) 0 ? null : Integer.valueOf(in.readInt());
        this.mMCC2 = in.readByte() == (byte) 0 ? null : Character.valueOf(in.createCharArray()[0]);
        this.mMNC2 = in.readByte() == (byte) 0 ? null : Character.valueOf(in.createCharArray()[0]);
        this.mReserved1 = in.readByte() == (byte) 0 ? null : Integer.valueOf(in.readInt());
        if (in.readByte() != (byte) 0) {
            str = in.readString();
        }
        this.mReserved2 = str;
    }

    private HubTrajectoryData(Integer id, Date mTimeStamp, Integer mCellID, Character mCellLAC, Integer mCellRssi, Character mMCC, Character mMNC, Integer mCellID1, Character mCellLAC1, Integer mCellRssi1, Character mMCC1, Character mMNC1, Integer mCellID2, Character mCellLAC2, Integer mCellRssi2, Character mMCC2, Character mMNC2, Integer mReserved1, String mReserved2) {
        this.id = id;
        this.mTimeStamp = mTimeStamp;
        this.mCellID = mCellID;
        this.mCellLAC = mCellLAC;
        this.mCellRssi = mCellRssi;
        this.mMCC = mMCC;
        this.mMNC = mMNC;
        this.mCellID1 = mCellID1;
        this.mCellLAC1 = mCellLAC1;
        this.mCellRssi1 = mCellRssi1;
        this.mMCC1 = mMCC1;
        this.mMNC1 = mMNC1;
        this.mCellID2 = mCellID2;
        this.mCellLAC2 = mCellLAC2;
        this.mCellRssi2 = mCellRssi2;
        this.mMCC2 = mMCC2;
        this.mMNC2 = mMNC2;
        this.mReserved1 = mReserved1;
        this.mReserved2 = mReserved2;
    }

    public int describeContents() {
        return 0;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
        setValue();
    }

    public Date getMTimeStamp() {
        return this.mTimeStamp;
    }

    public void setMTimeStamp(Date mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
        setValue();
    }

    public Integer getMCellID() {
        return this.mCellID;
    }

    public void setMCellID(Integer mCellID) {
        this.mCellID = mCellID;
        setValue();
    }

    public Character getMCellLAC() {
        return this.mCellLAC;
    }

    public void setMCellLAC(Character mCellLAC) {
        this.mCellLAC = mCellLAC;
        setValue();
    }

    public Integer getMCellRssi() {
        return this.mCellRssi;
    }

    public void setMCellRssi(Integer mCellRssi) {
        this.mCellRssi = mCellRssi;
        setValue();
    }

    public Character getMMCC() {
        return this.mMCC;
    }

    public void setMMCC(Character mMCC) {
        this.mMCC = mMCC;
        setValue();
    }

    public Character getMMNC() {
        return this.mMNC;
    }

    public void setMMNC(Character mMNC) {
        this.mMNC = mMNC;
        setValue();
    }

    public Integer getMCellID1() {
        return this.mCellID1;
    }

    public void setMCellID1(Integer mCellID1) {
        this.mCellID1 = mCellID1;
        setValue();
    }

    public Character getMCellLAC1() {
        return this.mCellLAC1;
    }

    public void setMCellLAC1(Character mCellLAC1) {
        this.mCellLAC1 = mCellLAC1;
        setValue();
    }

    public Integer getMCellRssi1() {
        return this.mCellRssi1;
    }

    public void setMCellRssi1(Integer mCellRssi1) {
        this.mCellRssi1 = mCellRssi1;
        setValue();
    }

    public Character getMMCC1() {
        return this.mMCC1;
    }

    public void setMMCC1(Character mMCC1) {
        this.mMCC1 = mMCC1;
        setValue();
    }

    public Character getMMNC1() {
        return this.mMNC1;
    }

    public void setMMNC1(Character mMNC1) {
        this.mMNC1 = mMNC1;
        setValue();
    }

    public Integer getMCellID2() {
        return this.mCellID2;
    }

    public void setMCellID2(Integer mCellID2) {
        this.mCellID2 = mCellID2;
        setValue();
    }

    public Character getMCellLAC2() {
        return this.mCellLAC2;
    }

    public void setMCellLAC2(Character mCellLAC2) {
        this.mCellLAC2 = mCellLAC2;
        setValue();
    }

    public Integer getMCellRssi2() {
        return this.mCellRssi2;
    }

    public void setMCellRssi2(Integer mCellRssi2) {
        this.mCellRssi2 = mCellRssi2;
        setValue();
    }

    public Character getMMCC2() {
        return this.mMCC2;
    }

    public void setMMCC2(Character mMCC2) {
        this.mMCC2 = mMCC2;
        setValue();
    }

    public Character getMMNC2() {
        return this.mMNC2;
    }

    public void setMMNC2(Character mMNC2) {
        this.mMNC2 = mMNC2;
        setValue();
    }

    public Integer getMReserved1() {
        return this.mReserved1;
    }

    public void setMReserved1(Integer mReserved1) {
        this.mReserved1 = mReserved1;
        setValue();
    }

    public String getMReserved2() {
        return this.mReserved2;
    }

    public void setMReserved2(String mReserved2) {
        this.mReserved2 = mReserved2;
        setValue();
    }

    public void writeToParcel(Parcel out, int ignored) {
        super.writeToParcel(out, ignored);
        if (this.id != null) {
            out.writeByte((byte) 1);
            out.writeInt(this.id.intValue());
        } else {
            out.writeByte((byte) 0);
            out.writeInt(1);
        }
        if (this.mTimeStamp != null) {
            out.writeByte((byte) 1);
            out.writeLong(this.mTimeStamp.getTime());
        } else {
            out.writeByte((byte) 0);
        }
        if (this.mCellID != null) {
            out.writeByte((byte) 1);
            out.writeInt(this.mCellID.intValue());
        } else {
            out.writeByte((byte) 0);
        }
        if (this.mCellLAC != null) {
            out.writeByte((byte) 1);
            out.writeCharArray(new char[]{this.mCellLAC.charValue()});
        } else {
            out.writeByte((byte) 0);
        }
        if (this.mCellRssi != null) {
            out.writeByte((byte) 1);
            out.writeInt(this.mCellRssi.intValue());
        } else {
            out.writeByte((byte) 0);
        }
        if (this.mMCC != null) {
            out.writeByte((byte) 1);
            out.writeCharArray(new char[]{this.mMCC.charValue()});
        } else {
            out.writeByte((byte) 0);
        }
        if (this.mMNC != null) {
            out.writeByte((byte) 1);
            out.writeCharArray(new char[]{this.mMNC.charValue()});
        } else {
            out.writeByte((byte) 0);
        }
        if (this.mCellID1 != null) {
            out.writeByte((byte) 1);
            out.writeInt(this.mCellID1.intValue());
        } else {
            out.writeByte((byte) 0);
        }
        if (this.mCellLAC1 != null) {
            out.writeByte((byte) 1);
            out.writeCharArray(new char[]{this.mCellLAC1.charValue()});
        } else {
            out.writeByte((byte) 0);
        }
        if (this.mCellRssi1 != null) {
            out.writeByte((byte) 1);
            out.writeInt(this.mCellRssi1.intValue());
        } else {
            out.writeByte((byte) 0);
        }
        if (this.mMCC1 != null) {
            out.writeByte((byte) 1);
            out.writeCharArray(new char[]{this.mMCC1.charValue()});
        } else {
            out.writeByte((byte) 0);
        }
        if (this.mMNC1 != null) {
            out.writeByte((byte) 1);
            out.writeCharArray(new char[]{this.mMNC1.charValue()});
        } else {
            out.writeByte((byte) 0);
        }
        if (this.mCellID2 != null) {
            out.writeByte((byte) 1);
            out.writeInt(this.mCellID2.intValue());
        } else {
            out.writeByte((byte) 0);
        }
        if (this.mCellLAC2 != null) {
            out.writeByte((byte) 1);
            out.writeCharArray(new char[]{this.mCellLAC2.charValue()});
        } else {
            out.writeByte((byte) 0);
        }
        if (this.mCellRssi2 != null) {
            out.writeByte((byte) 1);
            out.writeInt(this.mCellRssi2.intValue());
        } else {
            out.writeByte((byte) 0);
        }
        if (this.mMCC2 != null) {
            out.writeByte((byte) 1);
            out.writeCharArray(new char[]{this.mMCC2.charValue()});
        } else {
            out.writeByte((byte) 0);
        }
        if (this.mMNC2 != null) {
            out.writeByte((byte) 1);
            out.writeCharArray(new char[]{this.mMNC2.charValue()});
        } else {
            out.writeByte((byte) 0);
        }
        if (this.mReserved1 != null) {
            out.writeByte((byte) 1);
            out.writeInt(this.mReserved1.intValue());
        } else {
            out.writeByte((byte) 0);
        }
        if (this.mReserved2 != null) {
            out.writeByte((byte) 1);
            out.writeString(this.mReserved2);
            return;
        }
        out.writeByte((byte) 0);
    }

    public AEntityHelper<HubTrajectoryData> getHelper() {
        return HubTrajectoryDataHelper.getInstance();
    }

    public String getEntityName() {
        return "com.huawei.nb.model.trajectory.HubTrajectoryData";
    }

    public String getDatabaseName() {
        return "dsServiceMetaData";
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("HubTrajectoryData { id: ").append(this.id);
        sb.append(", mTimeStamp: ").append(this.mTimeStamp);
        sb.append(", mCellID: ").append(this.mCellID);
        sb.append(", mCellLAC: ").append(this.mCellLAC);
        sb.append(", mCellRssi: ").append(this.mCellRssi);
        sb.append(", mMCC: ").append(this.mMCC);
        sb.append(", mMNC: ").append(this.mMNC);
        sb.append(", mCellID1: ").append(this.mCellID1);
        sb.append(", mCellLAC1: ").append(this.mCellLAC1);
        sb.append(", mCellRssi1: ").append(this.mCellRssi1);
        sb.append(", mMCC1: ").append(this.mMCC1);
        sb.append(", mMNC1: ").append(this.mMNC1);
        sb.append(", mCellID2: ").append(this.mCellID2);
        sb.append(", mCellLAC2: ").append(this.mCellLAC2);
        sb.append(", mCellRssi2: ").append(this.mCellRssi2);
        sb.append(", mMCC2: ").append(this.mMCC2);
        sb.append(", mMNC2: ").append(this.mMNC2);
        sb.append(", mReserved1: ").append(this.mReserved1);
        sb.append(", mReserved2: ").append(this.mReserved2);
        sb.append(" }");
        return sb.toString();
    }

    public boolean equals(Object o) {
        return super.equals(o);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public String getDatabaseVersion() {
        return "0.0.11";
    }

    public int getDatabaseVersionCode() {
        return 11;
    }

    public String getEntityVersion() {
        return "0.0.1";
    }

    public int getEntityVersionCode() {
        return 1;
    }
}
