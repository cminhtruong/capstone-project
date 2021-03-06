package dalker.cmtruong.com.app.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import dalker.cmtruong.com.app.database.ListConverter;

/**
 * User model
 *
 * @author davidetruong
 * @version 1.0
 * @since 31th July, 2018
 */
@Entity(tableName = "users")
@TypeConverters(ListConverter.class)
public class User implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "idUser")
    @NotNull
    private String idUser;

    @SerializedName("gender")
    private String gender;

    @SerializedName("name")
    @Embedded
    private Name name;

    @SerializedName("location")
    @Embedded
    private Location location;

    @SerializedName("email")
    private String email;

    @SerializedName("login")
    @Embedded
    private Login login;

    @SerializedName("dob")
    @Embedded
    private Dob dob;

    @SerializedName("phone")
    private String phone;

    @SerializedName("picture")
    @Embedded
    private Picture pictureURL;

    @SerializedName("nat")
    private String nat;

    private String description;

    private float price;

    private int dogNumber;


    private ArrayList<Review> reviews = new ArrayList<>();


    @Ignore
    public User(String gender, Login login, Name name, Location location, String email, Dob dob, String phone, Picture pictureURL, String nat, String description, float price, int dogNumber, ArrayList<Review> reviews) {
        this.gender = gender;
        this.login = login;
        this.name = name;
        this.location = location;
        this.email = email;
        this.dob = dob;
        this.phone = phone;
        this.pictureURL = pictureURL;
        this.nat = nat;
        this.description = description;
        this.price = price;
        this.dogNumber = dogNumber;
        this.reviews.addAll(reviews);
    }


    public User() {
    }

    protected User(Parcel in) {
        idUser = in.readString();
        gender = in.readString();
        name = in.readParcelable(Name.class.getClassLoader());
        location = in.readParcelable(Location.class.getClassLoader());
        email = in.readString();
        login = in.readParcelable(Login.class.getClassLoader());
        dob = in.readParcelable(Dob.class.getClassLoader());
        phone = in.readString();
        pictureURL = in.readParcelable(Picture.class.getClassLoader());
        nat = in.readString();
        description = in.readString();
        price = in.readFloat();
        reviews = in.createTypedArrayList(Review.CREATOR);
        dogNumber = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idUser);
        dest.writeString(gender);
        dest.writeParcelable(name, flags);
        dest.writeParcelable(location, flags);
        dest.writeString(email);
        dest.writeParcelable(login, flags);
        dest.writeParcelable(dob, flags);
        dest.writeString(phone);
        dest.writeParcelable(pictureURL, flags);
        dest.writeString(nat);
        dest.writeString(description);
        dest.writeFloat(price);
        dest.writeTypedList(reviews);
        dest.writeInt(dogNumber);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getDogNumber() {
        return dogNumber;
    }

    public void setDogNumber(int dogNumber) {
        this.dogNumber = dogNumber;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Dob getDob() {
        return dob;
    }

    public void setDob(Dob dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Picture getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(Picture pictureURL) {
        this.pictureURL = pictureURL;
    }

    public String getNat() {
        return nat;
    }

    public void setNat(String nat) {
        this.nat = nat;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }


    @Override
    public String toString() {
        return "{" +
                "idUser='" + idUser + '\'' +
                ", gender='" + gender + '\'' +
                ", name=" + name +
                ", location=" + location +
                ", email='" + email + '\'' +
                ", login=" + login +
                ", dob=" + dob +
                ", phone='" + phone + '\'' +
                ", pictureURL=" + pictureURL +
                ", nat='" + nat + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", dogNumber=" + dogNumber +
                ", reviews=" + reviews +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
