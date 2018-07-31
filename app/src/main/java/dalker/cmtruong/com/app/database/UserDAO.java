package dalker.cmtruong.com.app.database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import dalker.cmtruong.com.app.model.User;

/**
 * @author davidetruong
 * @version 1.0
 * @since 31th July, 2018
 */
@Dao
public interface UserDAO {

    @Query("SELECT * FROM users")
    LiveData<List<User>> loadAllUsers();

    @Insert
    void insertUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("SELECT * FROM users WHERE id = :id")
    LiveData<User> loadUserById(String id);
}
