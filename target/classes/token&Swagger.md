# 4. Cambiar tiempo de expiración

Si en el [aplication.yml](/src/main/resources/application.yml) cambiamos el ttl de tiempo muy corto (de 1h a 30 segundos) 

```
token-ttl-seconds: 30
```

se pueden observar los cambios de mejor forma.

1. Se reinicia la app y se genera el auth login verificando que trae un `expires_in 30` que son 30 segundos de expiración del token

![](/src/main/resources/photos/TTL.png)

2. Probamos el endpoint de GET a los blueprints y obtenemos el resultados

![](/src/main/resources/photos/get.png)

3. Después de los 30 segundos, volvemos a probar el mismo endpoint pero ahora tenemos este resultado

![](/src/main/resources/photos/expires.png)

Lo que nos indica que en efecto, el token generado expiró al tiempo que nosotros le implementamos.
Cosa que también podemos verificar gracias a jwt.io

![](/src/main/resources/photos/jwtDecode.png)

En el podemos observar que `iat`, tiempo en el que empieza a contar el token fue a las 18:35:55
y después, observamos que el `exp`, tiempo en el que termina el token fue a las 18:36.:25

Los exactos 30 segundos que esperabamos.

---

# 5. Swagger