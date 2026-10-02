import flet as ft
from urllib.parse import urlparse

from controllers.product_controller import ProductController


class ProductFormView(ft.View):

    def __init__(
        self,
        session: dict,
        mode: str = "create",
        product_id: int | None = None,
        on_success=None,
        on_cancel=None
    ):

        super().__init__(
            route="/product/new"
            if mode == "create"
            else f"/product/edit/{product_id}"
        )

        self.session = session
        self.mode = mode
        self.product_id = product_id
        self.on_success = on_success
        self.on_cancel = on_cancel

        # Campos
        self.txt_title = ft.TextField(
            label="Título",
            width=500
        )

        self.txt_price = ft.TextField(
            label="Precio",
            width=500
        )

        self.txt_description = ft.TextField(
            label="Descripción",
            width=500,
            multiline=True,
            min_lines=4,
            max_lines=6
        )

        self.txt_category = ft.TextField(
            label="Categoría",
            width=500
        )

        self.txt_image = ft.TextField(
            label="URL de imagen",
            width=500
        )

        self.lbl_error = ft.Text(
            color=ft.Colors.RED_500
        )

        self.btn_guardar = ft.ElevatedButton(
            "Guardar producto"
            if mode == "create"
            else "Guardar cambios",
            icon=ft.Icons.SAVE,
            on_click=self.guardar
        )

        self.btn_cancelar = ft.TextButton(
            "Cancelar",
            on_click=lambda e: self._cancelar()
        )

        titulo = (
            "Agregar nuevo producto al catálogo"
            if mode == "create"
            else "Editar producto"
        )

        self.controls = [

            ft.Container(
                expand=True,
                alignment=ft.Alignment(0, 0),

                content=ft.Column(

                    controls=[

                        ft.Text(
                            titulo,
                            size=24,
                            weight=ft.FontWeight.BOLD
                        ),

                        self.txt_title,

                        self.txt_price,

                        self.txt_description,

                        self.txt_category,

                        self.txt_image,

                        self.lbl_error,

                        ft.Row(
                            controls=[
                                self.btn_guardar,
                                self.btn_cancelar
                            ],
                            alignment=ft.MainAxisAlignment.CENTER
                        )
                    ],

                    horizontal_alignment=ft.CrossAxisAlignment.CENTER,
                    spacing=12,
                    scroll=ft.ScrollMode.AUTO
                )
            )
        ]

    async def cargar_producto(self):

        if self.mode != "edit":
            return

        try:

            producto = await ProductController.obtener_detalle(
                self.product_id
            )

            if not producto:

                self.lbl_error.value = (
                    "No se encontró el producto."
                )

                if self.page:
                    self.page.update()

                return

            self.txt_title.value = producto.title

            self.txt_price.value = str(
                producto.price
            )

            self.txt_description.value = (
                producto.description
            )

            self.txt_category.value = (
                producto.category
            )

            self.txt_image.value = (
                producto.image
            )

            if self.page:
                self.page.update()

        except Exception as err:

            self.lbl_error.value = (
                f"Error al cargar el producto: {err}"
            )

            if self.page:
                self.page.update()

    def _cancelar(self):

        if self.on_cancel:
            self.on_cancel()

    def _validar_url(self, url: str) -> bool:

        try:

            resultado = urlparse(url)

            return (
                resultado.scheme in ("http", "https")
                and bool(resultado.netloc)
            )

        except Exception:
            return False

    def _validar(self):

        errores = []

        title = self.txt_title.value.strip()
        price_text = self.txt_price.value.strip()
        description = self.txt_description.value.strip()
        category = self.txt_category.value.strip()
        image = self.txt_image.value.strip()

        if not title:
            errores.append("El título es obligatorio.")

        if not price_text:
            errores.append("El precio es obligatorio.")
        else:

            try:
                float(price_text)
            except ValueError:
                errores.append(
                    "El precio debe ser numérico."
                )

        if not description:
            errores.append(
                "La descripción es obligatoria."
            )

        if not category:
            errores.append(
                "La categoría es obligatoria."
            )

        if not image:
            errores.append(
                "La URL de imagen es obligatoria."
            )
        elif not self._validar_url(image):
            errores.append(
                "La URL de imagen no es válida."
            )

        return errores

    async def guardar(self, e):

        self.lbl_error.value = ""

        errores = self._validar()

        if errores:

            self.lbl_error.value = "\n".join(
                errores
            )

            if self.page:
                self.page.update()

            return

        # Desactivar botón durante petición
        self.btn_guardar.disabled = True
        self.btn_guardar.text = "Guardando..."
        self.btn_guardar.icon = ft.Icons.HOURGLASS_TOP

        if self.page:
            self.page.update()

        try:

            title = self.txt_title.value.strip()

            price = float(
                self.txt_price.value.strip()
            )

            description = (
                self.txt_description.value.strip()
            )

            category = (
                self.txt_category.value.strip()
            )

            image = (
                self.txt_image.value.strip()
            )

            # =========================
            # CREAR
            # =========================
            if self.mode == "create":

                producto = (
                    await ProductController.crear_producto(
                        self.session,
                        title,
                        price,
                        description,
                        category,
                        image
                    )
                )

                self.btn_guardar.disabled = False

                if self.page:
                    self.page.update()

                await self._mostrar_creado(
                    producto
                )

                # Limpiar formulario
                self.txt_title.value = ""
                self.txt_price.value = ""
                self.txt_description.value = ""
                self.txt_category.value = ""
                self.txt_image.value = ""

                if self.page:
                    self.page.update()

                return

            # =========================
            # EDITAR
            # =========================
            producto = (
                await ProductController.actualizar_producto(
                    self.session,
                    self.product_id,
                    title,
                    price,
                    description,
                    category,
                    image
                )
            )

            self.btn_guardar.disabled = False

            if self.page:
                self.page.update()

            await self._mostrar_actualizado()

            if self.on_success:
                self.on_success(producto)

        except Exception as err:

            self.btn_guardar.disabled = False
            self.btn_guardar.text = (
                "Guardar producto"
                if self.mode == "create"
                else "Guardar cambios"
            )
            self.btn_guardar.icon = ft.Icons.SAVE

            self.lbl_error.value = (
                f"Error: {err}"
            )

            if self.page:
                self.page.update()

    async def _mostrar_creado(
        self,
        producto
    ):

        if not self.page:
            return

        dialogo = ft.AlertDialog(
            title=ft.Text(
                "Producto creado"
            ),

            content=ft.Text(
                "Producto creado correctamente.\n\n"
                f"ID generado: {producto.id}"
            ),

            actions=[
                ft.TextButton(
                    "Aceptar",
                    on_click=lambda e:
                    self._cerrar_creado(
                        dialogo
                    )
                )
            ]
        )

        self.page.dialog = dialogo
        dialogo.open = True
        self.page.update()

    def _cerrar_creado(
        self,
        dialogo
    ):

        dialogo.open = False
        self.page.update()

        # Después de crear, volver al catálogo
        if self.on_success:
            self.on_success(None)

    async def _mostrar_actualizado(self):

        if not self.page:
            return

        snack = ft.SnackBar(
            content=ft.Text(
                "Producto actualizado (Simulación)"
            )
        )

        self.page.overlay.append(snack)
        snack.open = True
        self.page.update()