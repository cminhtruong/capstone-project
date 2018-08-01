package dalker.cmtruong.com.app.model;

import com.google.gson.annotations.SerializedName;

/**
 * Name model
 *
 * @author davidetruong
 * @version 1.0
 * @since July 31th, 2018
 */
public class Name {

    private String title;

    @SerializedName("first")
    private String firstName;

    @SerializedName("last")
    private String lastName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Name{" +
                "title='" + title + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
