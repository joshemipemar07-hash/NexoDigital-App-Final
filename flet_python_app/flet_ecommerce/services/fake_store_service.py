import httpx
from models.product import Product


# Configuración base y cabeceras para las peticiones a la Fake Store API
BASE_URL = "https://fakestoreapi.com"

HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)",
    "Accept": "application/json"
}


class FakeStoreService:

    # Verificación inicial de conectividad con el servidor
    @staticmethod
    async def verificar_conexion() -> bool:
        try:
            async with httpx.AsyncClient(
                timeout=10.0,
                follow_redirects=True
            ) as client:

                res = await client.get(
                    f"{BASE_URL}/products?limit=1",
                    headers=HEADERS
                )

                return res.status_code in (200, 201)

        except Exception as e:
            print(f"[CHECK CON_ERROR]: {e}")
            return False


    # Autenticación de usuarios contra la API
    @staticmethod
    async def login(username: str, password: str) -> dict | None:
        try:
            async with httpx.AsyncClient(
                timeout=10.0,
                follow_redirects=True
            ) as client:

                res = await client.post(
                    f"{BASE_URL}/auth/login",
                    json={
                        "username": username,
                        "password": password
                    },
                    headers=HEADERS
                )

                if res.status_code in (200, 201):

                    token = res.json().get("token")

                    user_info = await FakeStoreService._obtener_info_usuario(
                        username
                    )

                    return {
                        "token": token,
                        "user": user_info
                    }

        except Exception as err:
            print(f"[API LOGIN ERROR]: {err}")

        return None


    # Obtener información del usuario
    @staticmethod
    async def _obtener_info_usuario(username: str) -> dict:
        try:
            async with httpx.AsyncClient(
                timeout=10.0,
                follow_redirects=True
            ) as client:

                res_users = await client.get(
                    f"{BASE_URL}/users",
                    headers=HEADERS
                )

                if res_users.status_code in (200, 201):

                    usuarios = res_users.json()

                    return next(
                        (
                            u for u in usuarios
                            if u.get("username") == username
                        ),
                        {}
                    )

        except Exception:
            pass

        return {}


    # Obtener todos los productos
    @staticmethod
    async def obtener_productos() -> list[Product]:

        async with httpx.AsyncClient(
            timeout=20.0,
            follow_redirects=True
        ) as client:

            res = await client.get(
                f"{BASE_URL}/products",
                headers=HEADERS
            )

            if res.status_code in (200, 201):

                return [
                    Product.from_json(p)
                    for p in res.json()
                ]

            raise Exception(
                f"Respuesta HTTP {res.status_code}"
            )


    # Obtener un producto por ID
    @staticmethod
    async def obtener_producto_por_id(
        product_id: int
    ) -> Product | None:

        async with httpx.AsyncClient(
            timeout=10.0,
            follow_redirects=True
        ) as client:

            res = await client.get(
                f"{BASE_URL}/products/{product_id}",
                headers=HEADERS
            )

            if res.status_code in (200, 201):

                data = res.json()

                if data:
                    return Product.from_json(data)

            return None


    # Obtener categorías
    @staticmethod
    async def obtener_categorias() -> list[str]:

        async with httpx.AsyncClient(
            timeout=10.0,
            follow_redirects=True
        ) as client:

            res = await client.get(
                f"{BASE_URL}/products/categories",
                headers=HEADERS
            )

            if res.status_code in (200, 201):

                return res.json()

            raise Exception(
                f"Respuesta HTTP {res.status_code}"
            )


    # Obtener productos por categoría
    @staticmethod
    async def obtener_productos_por_categoria(
        categoria: str
    ) -> list[Product]:

        async with httpx.AsyncClient(
            timeout=20.0,
            follow_redirects=True
        ) as client:

            res = await client.get(
                f"{BASE_URL}/products/category/{categoria}",
                headers=HEADERS
            )

            if res.status_code in (200, 201):

                return [
                    Product.from_json(p)
                    for p in res.json()
                ]

            raise Exception(
                f"Respuesta HTTP {res.status_code}"
            )


    # Crear producto
    @staticmethod
    async def crear_producto(
        title: str,
        price: float,
        description: str,
        category: str,
        image: str
    ) -> Product:

        async with httpx.AsyncClient(
            timeout=20.0,
            follow_redirects=True
        ) as client:

            res = await client.post(
                f"{BASE_URL}/products",
                json={
                    "title": title,
                    "price": price,
                    "description": description,
                    "category": category,
                    "image": image
                },
                headers=HEADERS
            )

            if res.status_code in (200, 201):

                data = res.json()

                return Product.from_json(data)

            raise Exception(
                f"Respuesta HTTP {res.status_code}"
            )


    # Actualizar producto
    @staticmethod
    async def actualizar_producto(
        product_id: int,
        title: str,
        price: float,
        description: str,
        category: str,
        image: str
    ) -> Product:

        async with httpx.AsyncClient(
            timeout=20.0,
            follow_redirects=True
        ) as client:

            res = await client.put(
                f"{BASE_URL}/products/{product_id}",
                json={
                    "title": title,
                    "price": price,
                    "description": description,
                    "category": category,
                    "image": image
                },
                headers=HEADERS
            )

            if res.status_code in (200, 201):

                data = res.json()

                return Product.from_json(data)

            raise Exception(
                f"Respuesta HTTP {res.status_code}"
            )


    # Eliminar producto
    @staticmethod
    async def eliminar_producto(
        product_id: int
    ):

        async with httpx.AsyncClient(
            timeout=20.0,
            follow_redirects=True
        ) as client:

            res = await client.delete(
                f"{BASE_URL}/products/{product_id}",
                headers=HEADERS
            )

            # DELETE correcto
            if res.status_code not in (200, 204):
                raise Exception(
                    f"Error al eliminar producto. "
                    f"Código HTTP: {res.status_code}"
                )

            # La Fake Store API puede devolver una respuesta vacía.
            # Por eso NO intentamos hacer res.json() directamente.
            if not res.text.strip():
                return None

            try:
                data = res.json()

                if data:
                    return Product.from_json(data)

            except ValueError:
                return None

            return None