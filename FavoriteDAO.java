package com.example.oderfoodapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.oderfoodapp.object.FavoriteFood;

import java.util.List;

@Dao
public interface FavoriteDAO {
    @Insert
    void insert(FavoriteFood favorite);

    @Update
    void update(FavoriteFood favorite);

    @Delete
    void delete(FavoriteFood favorite);

    @Query("SELECT * FROM tblFavorite")
    List<FavoriteFood> getAllFavorites();

    @Query("SELECT * FROM tblFavorite WHERE foodID = :foodID")
    List<FavoriteFood> getAllFavoritesByName(String foodID);

    @Query("SELECT EXISTS(SELECT * FROM tblFavorite WHERE foodID = :foodID AND username = :username)")
    boolean isFavorite(String foodID, String username);

    @Query("DELETE FROM tblFavorite WHERE username = :username AND foodID = :foodID")
    void deleteByUserAndFood(String username, String foodID);

    @Query("SELECT ff.favoriteID, ff.username, ff.foodID, ff.image, ff.price, f.name AS food_name " +
            "FROM tblFavorite ff " +
            "INNER JOIN tblFood f ON ff.foodID = f.foodID " +
            "WHERE ff.username = :username")
    List<FavoriteFood> getFavoriteFoodsWithName(String username);
}
