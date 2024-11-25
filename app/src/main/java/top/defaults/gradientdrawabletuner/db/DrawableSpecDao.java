package top.defaults.gradientdrawabletuner.db;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface DrawableSpecDao {

    @Query("SELECT * FROM drawable_spec")
    LiveData<List<DrawableSpec>> getAll();

    @Query("SELECT * FROM drawable_spec WHERE id = (:id)")
    DrawableSpec findById(long id);

    @Insert(onConflict = IGNORE)
    void insertAll(DrawableSpec... drawableSpec);

    @Insert(onConflict = IGNORE)
    long insert(DrawableSpec drawableSpec);

    @Update(onConflict = REPLACE)
    void update(DrawableSpec drawableSpec);

    @Delete
    void delete(DrawableSpec drawableSpec);
}
