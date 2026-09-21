import httpx

BASE_URL = "https://fakestoreapi.com"
HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
    "Accept": "application/json"
}

class Product:
    def __init__(self, id: int, title: str, price: float, description: str, category: str, image: str):
        self.id = id
        self.title = title
        self.price = price
        self.description = description
        self.category = category
        self.image = image

    @classmethod
    def from_json(cls, data: dict):
        return cls(
            id=data.get("id", 0),
            title=data.get("title", ""),
            price=float(data.get("price", 0.0)),
            description=data.get("description", ""),
            category=data.get("category", ""),
            image=data.get("image", "")
        )

class FakeStoreAPI:
    @staticmethod
    async def verificar_conexion() -> bool:
        """Comprueba si hay conexión de manera asíncrona."""
        try:
            async with httpx.AsyncClient(timeout=10.0, follow_redirects=True) as client:
                res = await client.get(f"{BASE_URL}/products?limit=1", headers=HEADERS)
                return res.status_code in (200, 201)
        except Exception as e:
            print(f"[CHECK CON_ERROR]: {e}")
            return False

    @staticmethod
    async def login(username: str, password: str) -> dict | None:
        """Autentica contra FakeStoreAPI de manera asíncrona."""
        try:
            async with httpx.AsyncClient(timeout=10.0, follow_redirects=True) as client:
                res = await client.post(
                    f"{BASE_URL}/auth/login",
                    json={"username": username, "password": password},
                    headers=HEADERS
                )
                if res.status_code in (200, 201):
                    token = res.json().get("token")
                    user_info = await FakeStoreAPI._obtener_info_usuario(username)
                    return {"token": token, "user": user_info}
        except Exception as err:
            print(f"[API LOGIN ERROR]: {err}")
            
        return None

    @staticmethod
    async def _obtener_info_usuario(username: str) -> dict:
        """Obtiene la información detallada del usuario."""
        try:
            async with httpx.AsyncClient(timeout=10.0, follow_redirects=True) as client:
                res_users = await client.get(f"{BASE_URL}/users", headers=HEADERS)
                if res_users.status_code in (200, 201):
                    usuarios = res_users.json()
                    return next((u for u in usuarios if u.get("username") == username), {})
        except Exception:
            pass
        return {}

    @staticmethod
    async def obtener_productos() -> list[Product]:
        """Obtiene el catálogo completo de productos."""
        async with httpx.AsyncClient(timeout=20.0, follow_redirects=True) as client:
            res = await client.get(f"{BASE_URL}/products", headers=HEADERS)
            if res.status_code in (200, 201):
                return [Product.from_json(p) for p in res.json()]
            raise Exception(f"Respuesta HTTP {res.status_code}")

    @staticmethod
    async def obtener_producto_por_id(product_id: int) -> Product | None:
        """Obtiene un producto por su ID."""
        async with httpx.AsyncClient(timeout=10.0, follow_redirects=True) as client:
            res = await client.get(f"{BASE_URL}/products/{product_id}", headers=HEADERS)
            if res.status_code in (200, 201):
                data = res.json()
                if data:
                    return Product.from_json(data)
            return None

    @staticmethod
    async def obtener_categorias() -> list[str]:
        """Escenario 1: Consume /products/categories."""
        async with httpx.AsyncClient(timeout=10.0, follow_redirects=True) as client:
            res = await client.get(f"{BASE_URL}/products/categories", headers=HEADERS)
            if res.status_code in (200, 201):
                return res.json()
            raise Exception(f"Respuesta HTTP {res.status_code}")

    @staticmethod
    async def obtener_productos_por_categoria(categoria: str) -> list[Product]:
        """Escenario 2: Consume /products/category/{category}."""
        async with httpx.AsyncClient(timeout=20.0, follow_redirects=True) as client:
            res = await client.get(f"{BASE_URL}/products/category/{categoria}", headers=HEADERS)
            if res.status_code in (200, 201):
                return [Product.from_json(p) for p in res.json()]
            raise Exception(f"Respuesta HTTP {res.status_code}")