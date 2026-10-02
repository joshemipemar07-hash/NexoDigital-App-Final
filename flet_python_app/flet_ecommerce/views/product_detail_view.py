import flet as ft

from controllers.product_controller import ProductController


class ProductDetailView(ft.View):

    def __init__(
        self,
        product_id: int,
        session: dict,
        on_back,
        on_edit
    ):
        super().__init__(
            route=f"/product/{product_id}"
        )

        self.product_id = product_id
        self.session = session
        self.on_back = on_back
        self.on_edit = on_edit

        self.appbar = ft.AppBar(
            title=ft.Text("Detalle del Producto"),
            leading=ft.IconButton(
                ft.Icons.ARROW_BACK,
                on_click=lambda e: self.on_back()
            )
        )

        self.content_area = ft.Container(
            expand=True,
            padding=20,
            content=ft.Column(
                controls=[
                    ft.ProgressRing(),
                    ft.Text("Cargando detalle...")
                ],
                alignment=ft.MainAxisAlignment.CENTER,
                horizontal_alignment=ft.CrossAxisAlignment.CENTER,
                expand=True
            )
        )

        self.controls = [
            self.content_area
        ]

    async def cargar_detalle(self):

        try:

            producto = await ProductController.obtener_detalle(
                self.product_id
            )

            if not producto:
                await self._mostrar_error(
                    "Producto no disponible."
                )
                return

            self._construir_ui(producto)

        except Exception as err:

            print(
                f"[PRODUCT DETAIL ERROR]: {err}"
            )

            await self._mostrar_error(
                f"Error al cargar el producto: {err}"
            )

    async def _mostrar_error(self, mensaje):

        if not self.page:
            return

        dialogo = ft.AlertDialog(
            title=ft.Text("Error"),
            content=ft.Text(mensaje),
            actions=[
                ft.TextButton(
                    "Aceptar",
                    on_click=lambda e:
                    self.page.pop_dialog()
                )
            ]
        )

        self.page.show_dialog(dialogo)

    def _construir_ui(self, producto):

        elementos = [

            ft.Image(
                src=producto.image,
                height=200,
                fit="contain"
            ),

            ft.Text(
                producto.title,
                size=20,
                weight=ft.FontWeight.BOLD
            ),

            ft.Text(
                f"Categoría: {producto.category.capitalize()}",
                size=14,
                color=ft.Colors.GREY_700
            ),

            ft.Text(
                f"${producto.price:.2f}",
                size=22,
                weight=ft.FontWeight.BOLD,
                color=ft.Colors.GREEN_700
            ),

            ft.Divider(),

            ft.Text(
                "Descripción:",
                size=16,
                weight=ft.FontWeight.BOLD
            ),

            ft.Text(
                producto.description,
                size=14
            )
        ]

        # =====================================
        # BOTONES SOLO PARA ADMINISTRADOR
        # =====================================

        if self.session.get("role") == "Administrador":

            elementos.append(
                ft.Divider()
            )

            elementos.append(
                ft.Row(
                    controls=[

                        # EDITAR
                        ft.ElevatedButton(
                            "Editar",
                            icon=ft.Icons.EDIT,
                            style=ft.ButtonStyle(
                                color=ft.Colors.WHITE,
                                bgcolor=ft.Colors.BLUE_600
                            ),
                            on_click=lambda e:
                            self.on_edit(
                                self.product_id
                            )
                        ),

                        # ELIMINAR
                        ft.ElevatedButton(
                            "Eliminar",
                            icon=ft.Icons.DELETE,
                            style=ft.ButtonStyle(
                                color=ft.Colors.WHITE,
                                bgcolor=ft.Colors.RED_600
                            ),
                            on_click=self._confirmar_eliminacion
                        )
                    ],

                    alignment=ft.MainAxisAlignment.CENTER,
                    spacing=15
                )
            )

        self.content_area.content = ft.Column(
            controls=elementos,
            spacing=12,
            scroll=ft.ScrollMode.AUTO,
            expand=True
        )

        if self.page:
            self.page.update()

    # =====================================
    # CONFIRMACIÓN DE ELIMINACIÓN
    # =====================================

    def _confirmar_eliminacion(self, e):

        dialogo = ft.AlertDialog(
            modal=True,

            title=ft.Text(
                "Confirmar eliminación"
            ),

            content=ft.Text(
                "¿Estás seguro de eliminar este producto?"
            ),

            actions=[

                # CANCELAR
                ft.TextButton(
                    "Cancelar",
                    on_click=lambda e:
                    self.page.pop_dialog()
                ),

                # CONFIRMAR ELIMINACIÓN
                ft.ElevatedButton(
                    "Eliminar",
                    icon=ft.Icons.DELETE,
                    on_click=self._eliminar_producto
                )
            ]
        )

        # ESTA ES LA PARTE IMPORTANTE
        self.page.show_dialog(dialogo)

    # =====================================
    # ELIMINAR PRODUCTO
    # =====================================

    async def _eliminar_producto(self, e):

        try:

            # Cerrar ventana de confirmación
            self.page.pop_dialog()

            # Hacer DELETE
            await ProductController.eliminar_producto(
                self.session,
                self.product_id
            )

            # Mostrar mensaje
            self.page.show_dialog(
                ft.SnackBar(
                    content=ft.Text(
                        "Producto eliminado correctamente"
                    )
                )
            )

            # Regresar al catálogo
            self.on_back()

        except Exception as err:

            print(
                f"[DELETE ERROR]: {err}"
            )

            self.page.show_dialog(
                ft.AlertDialog(
                    title=ft.Text("Error"),
                    content=ft.Text(
                        f"No se pudo eliminar el producto:\n{err}"
                    ),
                    actions=[
                        ft.TextButton(
                            "Aceptar",
                            on_click=lambda e:
                            self.page.pop_dialog()
                        )
                    ]
                )
            )