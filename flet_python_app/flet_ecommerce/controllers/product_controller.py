from services.fake_store_service import FakeStoreService
from models.product import Product


class ProductController:

    # Productos creados durante la ejecución
    _productos_creados: dict[int, Product] = {}

    # Productos modificados durante la ejecución
    _actualizaciones_locales: dict[int, Product] = {}

    # Productos eliminados durante la ejecución
    _productos_eliminados: set[int] = set()

    @staticmethod
    async def obtener_categorias() -> list[str]:
        return await FakeStoreService.obtener_categorias()

    @staticmethod
    async def obtener_catalogo(
        categoria: str = "TODAS"
    ) -> list[Product]:

        # Obtener productos desde la API
        if categoria and categoria != "TODAS":
            productos = await FakeStoreService.obtener_productos_por_categoria(
                categoria.lower()
            )
        else:
            productos = await FakeStoreService.obtener_productos()

        # Aplicar actualizaciones locales
        for i, producto in enumerate(productos):

            if producto.id in ProductController._actualizaciones_locales:
                productos[i] = (
                    ProductController._actualizaciones_locales[
                        producto.id
                    ]
                )

        # Agregar productos creados localmente
        for producto in ProductController._productos_creados.values():

            if (
                categoria == "TODAS"
                or producto.category.lower()
                == categoria.lower()
            ):
                if producto.id not in ProductController._productos_eliminados:
                    productos.append(producto)

        # Ocultar productos eliminados
        productos = [
            producto
            for producto in productos
            if producto.id
            not in ProductController._productos_eliminados
        ]

        return productos

    @staticmethod
    async def obtener_detalle(
        product_id: int
    ) -> Product | None:

        # Si fue eliminado localmente, no mostrarlo
        if product_id in ProductController._productos_eliminados:
            return None

        # Producto creado localmente
        if product_id in ProductController._productos_creados:

            producto = ProductController._productos_creados[
                product_id
            ]

        else:

            # Producto de la API
            producto = await FakeStoreService.obtener_producto_por_id(
                product_id
            )

        if producto is None:
            return None

        # Aplicar actualización local
        if product_id in ProductController._actualizaciones_locales:
            producto = ProductController._actualizaciones_locales[
                product_id
            ]

        return producto

    @staticmethod
    async def crear_producto(
        session: dict,
        title: str,
        price: float,
        description: str,
        category: str,
        image: str
    ) -> Product:

        if session.get("role") != "Administrador":
            raise PermissionError(
                "No tienes permisos para crear productos."
            )

        producto = await FakeStoreService.crear_producto(
            title,
            price,
            description,
            category.lower(),
            image
        )

        # Guardar localmente
        ProductController._productos_creados[
            producto.id
        ] = producto

        return producto

    @staticmethod
    async def actualizar_producto(
        session: dict,
        product_id: int,
        title: str,
        price: float,
        description: str,
        category: str,
        image: str
    ) -> Product:

        if session.get("role") != "Administrador":
            raise PermissionError(
                "No tienes permisos para editar productos."
            )

        producto = await FakeStoreService.actualizar_producto(
            product_id,
            title,
            price,
            description,
            category.lower(),
            image
        )

        # Guardar modificación local
        ProductController._actualizaciones_locales[
            product_id
        ] = producto

        # Si fue creado durante esta ejecución,
        # actualizar también su registro local.
        if product_id in ProductController._productos_creados:
            ProductController._productos_creados[
                product_id
            ] = producto

        return producto

    @staticmethod
    async def eliminar_producto(
        session: dict,
        product_id: int
    ) -> Product:

        if session.get("role") != "Administrador":
            raise PermissionError(
                "No tienes permisos para eliminar productos."
            )

        producto = await FakeStoreService.eliminar_producto(
            product_id
        )

        # Marcar como eliminado localmente
        ProductController._productos_eliminados.add(
            product_id
        )

        # Eliminar de listas locales
        ProductController._productos_creados.pop(
            product_id,
            None
        )

        ProductController._actualizaciones_locales.pop(
            product_id,
            None
        )

        return producto