package com.fenlan.storm.GeoIP2;

import com.fenlan.storm.Properties.FileProperties;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class AnalyzeIP {

    private static String dbFile = FileProperties.getGeoLitePath();

    public static String cityOfIP(String ip) throws IOException, GeoIp2Exception {
        // A File object pointing to your GeoIP2 or GeoLite2 database
        File database = new File(dbFile);
        System.out.println(dbFile);

        // This creates the DatabaseReader object. To improve performance, reuse
        // the object across lookups. The object is thread-safe
        DatabaseReader reader = new DatabaseReader.Builder(database).build();
        InetAddress ipAddress = InetAddress.getByName(ip);

        // Replace "city" with the appropriate method for your database, e.g.,
        // "country".
        CityResponse response = reader.city(ipAddress);

        City city = response.getCity();

        return city.getName();
    }
}
