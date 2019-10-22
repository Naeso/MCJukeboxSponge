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
        String uri = "jdbc:h2:./config/mcjukebox/region.db";
        if (sql == null) {
            sql = Sponge.getServiceManager().provide(SqlService.class).get();
        }
        return sql.getDataSource(uri);
    }

    public void createDatabase(){
        try (Connection conn = getDataSource().getConnection()) {
            PreparedStatement tableRegion = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS Region (" +
                            "RegionID Varchar(100) NOT NULL PRIMARY KEY," +
                            "URL Varchar(300) NOT NULL)");
            tableRegion.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean doesIDRegionExistsInDatabase(String IDRegion){
        try (Connection conn = getDataSource().getConnection()) {
            PreparedStatement tryRegion = conn.prepareStatement(
                    "SELECT RegionID FROM Region WHERE RegionID=?");
            tryRegion.setObject(1, IDRegion);
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

    public String getURLRegion(String IDRegion){
        if(doesIDRegionExistsInDatabase(IDRegion)){
            try (Connection conn = getDataSource().getConnection()) {
                PreparedStatement tryRegion = conn.prepareStatement(
                        "SELECT URL FROM Region WHERE RegionID=?");
                tryRegion.setString(1, IDRegion);
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

    public void setNewRegion(String IDRegion, String URLRegion){
        System.out.println("Searching for ID: " + IDRegion + " URL : " + URLRegion);
        try (Connection conn = getDataSource().getConnection()) {
            PreparedStatement tryRegion = conn.prepareStatement(
                    "INSERT INTO Region(RegionID, URL) VALUES (?, ?)");
            tryRegion.setObject(1, IDRegion);
            tryRegion.setObject(2, URLRegion);
            tryRegion.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updateURLRegion(String IDRegion, String URLRegion){
        System.out.println("Searching for ID: " + IDRegion + " URL : " + URLRegion);
        try (Connection conn = getDataSource().getConnection()) {
            PreparedStatement tryRegion = conn.prepareStatement(
                    "UPDATE Region SET URL=? WHERE RegionID=?");
            tryRegion.setObject(1, URLRegion);
            tryRegion.setObject(2, IDRegion);
            tryRegion.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteRegion(String IDRegion){
        try (Connection conn = getDataSource().getConnection()) {
            PreparedStatement tryRegion = conn.prepareStatement(
                    "DELETE FROM Region WHERE RegionID=?");
            tryRegion.setObject(1, IDRegion);
            tryRegion.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
