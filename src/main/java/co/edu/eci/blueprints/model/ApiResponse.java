package co.edu.eci.blueprints.model;

public record ApiResponse<T>(int code, String message, T data) {}