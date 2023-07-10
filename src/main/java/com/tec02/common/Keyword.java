/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.common;

/**
 *
 * @author Administrator
 */
public final class Keyword {

    public static final class FilterName {

        public static final String LINE = "Line";
        public static final String STATION = "Station";
        public static final String PRODUCT = "Product";
    }

    public static final class Url {
        public static final String LOGIN = "api.url.login";

        public static final class Product {
            public static final String GET = "api.url.product.get";
            public static final String POST = "api.url.product.post";
            public static final String DELETE = "api.url.product.delete";
            public static final String PUT = "api.url.product.put";
        }
        
        public static final class Station {
            public static final String GET = "api.url.station.get";
            public static final String POST = "api.url.station.post";
            public static final String DELETE = "api.url.station.delete";
            public static final String PUT = "api.url.station.put";
        }
        
        public static final class Line {
            public static final String GET = "api.url.line.get";
            public static final String POST = "api.url.line.post";
            public static final String DELETE = "api.url.line.delete";
            public static final String PUT = "api.url.line.put";
        }
        
        public static final class Pc {
            public static final String GET = "api.url.pc.get";
            public static final String POST = "api.url.pc.post";
            public static final String DELETE = "api.url.pc.delete";
            public static final String PUT = "api.url.pc.put";
        }
        
        public static final class Fgroup {
            public static final String GET = "api.url.fgroup.get";
            public static final String GET_LIST = "api.url.fgroup.getlist";
            public static final String POST = "api.url.fgroup.post";
            public static final String DELETE = "api.url.fgroup.delete";
            public static final String PUT = "api.url.fgroup.put";
        }
        
        public static final class File {
            public static final String GET = "api.url.file.get";
            public static final String GET_LAST_VERSION_DOWNLOAD = "api.url.file.getversiondownload";
            public static final String GET_VERSION = "api.url.file.getversion";
            public static final String POST = "api.url.file.post";
            public static final String DELETE = "api.url.file.delete";
            public static final String PUT = "api.url.file.put";
        }
    }
}
