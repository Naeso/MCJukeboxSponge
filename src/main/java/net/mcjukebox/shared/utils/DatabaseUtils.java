package net.mcjukebox.shared.utils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtils {
    private SqlService sql;

    public DatabaseUtils() { }

    private DataSource getDataSource() throws SQLException {
        String uri = "jdbc:h2:./config/mcjukebox/jukeboxdatabase.db";
        if (sql == null) {
            sql = Sponge.getServiceManager().provide(SqlService.class).get();
        }
        return sql.getDataSource(uri);
    }

    public void createDatabase(){
        try (Connection conn = getDataSource().getConnection()) {
            PreparedStatement tableRegion = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS Regions (" +
                            "RegionName Varchar(100) NOT NULL PRIMARY KEY," +
                            "URL Varchar(300) NOT NULL)");
            tableRegion.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }

        //Wait for the biome update... *wink* *wink*
        /*try (Connection conn = getDataSource().getConnection()) {
            PreparedStatement tableRegion = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS Biomes (" +
                            "BiomesID Varchar(100) NOT NULL PRIMARY KEY," +
                            "URL Varchar(300) NOT NULL)");
            tableRegion.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }*/
    }

    public boolean doesRegionExistsInDatabase(String NameRegion){
        try (Connection conn = getDataSource().getConnection()) {
            PreparedStatement tryRegion = conn.prepareStatement(
                    "SELECT RegionName FROM Regions WHERE RegionName=?");
            tryRegion.setObject(1, NameRegion);
            ResultSet resultSet = tryRegion.executeQuery();
            if (resultSet.next()){
                if(resultSet.wasNull()){
                    resultSet.close();
                    conn.close();
                    return false;
                }
                resultSet.close();
                conn.close();
                return true;
            }
        } catch (SQLException e){
            e.printStackTrace();

            return false;
        }
        return false;
    }

    public String getURLRegion(String NameRegion){
        if(doesRegionExistsInDatabase(NameRegion)){
            try (Connection conn = getDataSource().getConnection()) {
                PreparedStatement tryRegion = conn.prepareStatement(
                        "SELECT URL FROM Regions WHERE RegionName=?");
                tryRegion.setString(1, NameRegion);
                ResultSet resultSet = tryRegion.executeQuery();
                if (resultSet.next()){
                    String url = resultSet.getString("URL");
                    resultSet.close();
                    conn.close();
                    return url;
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
        return "";
    }

    public void setNewRegion(String NameRegion, String URLRegion){
        if(!doesRegionExistsInDatabase(NameRegion)){
            try (Connection conn = getDataSource().getConnection()) {
                PreparedStatement tryRegion = conn.prepareStatement(
                        "INSERT INTO Regions(RegionName, URL) VALUES (?, ?)");
                tryRegion.setObject(1, NameRegion);
                tryRegion.setObject(2, URLRegion);
                tryRegion.execute();
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public void updateURLRegion(String NameRegion, String URLRegion){
        if(doesRegionExistsInDatabase(NameRegion)){
            try (Connection conn = getDataSource().getConnection()) {
                PreparedStatement tryRegion = conn.prepareStatement(
                        "UPDATE Regions SET URL=? WHERE RegionName=?");
                tryRegion.setObject(1, URLRegion);
                tryRegion.setObject(2, NameRegion);
                tryRegion.execute();
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public void deleteRegion(String NameRegion){
        if(doesRegionExistsInDatabase(NameRegion)){
            try (Connection conn = getDataSource().getConnection()) {
                PreparedStatement tryRegion = conn.prepareStatement(
                        "DELETE FROM Regions WHERE RegionName=?");
                tryRegion.setObject(1, NameRegion);
                tryRegion.executeUpdate();
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
}
