import axios from "axios";

// Configuración base de la API - URL obtenida desde variables de entorno o valor por defecto
const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

// Instancia base de axios con configuración común
export const api = axios.create({
  baseURL: API_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

/* ======================================================
   GESTIÓN DE PRODUCTOS
====================================================== */

// Obtener todos los productos del sistema
export const getProductos = () => api.get("/productos");

// Crear un nuevo producto en la base de datos
export const crearProducto = (data) => api.post("/productos", data);

// Actualizar un producto existente por ID
export const actualizarProducto = (id, data) => api.put(`/productos/${id}`, data);

// Eliminar permanentemente un producto por ID
export const eliminarProducto = (id) => api.delete(`/productos/${id}`);

// Obtener productos marcados como destacados
export const getProductosDestacados = () => api.get("/productos/destacados");

/* ======================================================
   GESTIÓN DE CATEGORÍAS
====================================================== */

// Obtener todas las categorías disponibles
export const getCategorias = () => api.get("/categorias");

/* ======================================================
   GESTIÓN DE USUARIOS
====================================================== */

// Obtener todos los usuarios registrados
export const getUsuarios = () => api.get("/usuarios");

// Obtener información específica de un usuario por ID
export const getUsuarioPorId = (id) => api.get(`/usuarios/${id}`);

// Crear un nuevo usuario con validaciones completas
export const crearUsuario = async (usuario) => {
  console.log("Enviando datos de usuario al backend:", usuario);

  // Validaciones de campos obligatorios y formatos
  if (!usuario.rut || usuario.rut.trim() === "") {
    throw new Error("El RUT es obligatorio");
  }

  // Validar formato de RUT chileno
  if (!usuario.rut.match(/^\d{1,2}\.\d{3}\.\d{3}-[\dkK]$/)) {
    throw new Error("Formato de RUT inválido. Use: 12.345.678-9");
  }

  if (!usuario.email || usuario.email.trim() === "") {
    throw new Error("El correo electrónico es obligatorio");
  }

  // Validar formato de email válido
  if (!usuario.email.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)) {
    throw new Error("Formato de email inválido");
  }

  if (!usuario.password || usuario.password.length < 6) {
    throw new Error("La contraseña debe tener al menos 6 caracteres");
  }

  // Estructurar payload final para envío al backend
  const payload = {
    username: usuario.username || "",
    email: usuario.email,
    rut: usuario.rut,
    password: usuario.password,
    role: usuario.role || "Cliente",
    status: usuario.status || "activo",
  };

  console.log("Payload final enviado al servidor:", payload);
  return api.post("/usuarios", payload);
};

// Actualizar información de usuario existente
export const actualizarUsuario = async (id, data) => {
  const payload = { ...data };

  // Validaciones para actualización
  if (!payload.rut || payload.rut.trim() === "") {
    throw new Error("El RUT es obligatorio");
  }

  // Validar formato de RUT en actualización
  if (!payload.rut.match(/^\d{1,2}\.\d{3}\.\d{3}-[\dkK]$/)) {
    throw new Error("Formato de RUT inválido. Use: 12.345.678-9");
  }

  // Validar email si está presente en la actualización
  if (payload.email && !payload.email.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)) {
    throw new Error("Formato de email inválido");
  }

  // Manejo de contraseña en actualización
  if (payload.password) {
    if (payload.password.length < 6) {
      throw new Error("La contraseña debe tener al menos 6 caracteres");
    }
    console.log("Enviando nueva contraseña para encriptación");
  } else {
    delete payload.password;
  }

  console.log(`Actualizando usuario ${id}:`, payload);
  return api.put(`/usuarios/${id}`, payload);
};

// Eliminar usuario del sistema
export const eliminarUsuario = (id) => api.delete(`/usuarios/${id}`);

/* ======================================================
   AUTENTICACIÓN Y SEGURIDAD
====================================================== */

// Iniciar sesión de usuario con credenciales
export const loginUsuario = (email, password) =>
  api.post("/auth/login", { email, password });

/* ======================================================
   GESTIÓN DEL CARRITO DE COMPRAS
====================================================== */

// Agregar item al carrito de compras
export const agregarItemCarrito = (usuarioId, productoId, quantity = 1) =>
  api.post("/itemscarrito", {
    usuario: { id: usuarioId },
    producto: { id: productoId },
    quantity,
  });

// Obtener todos los items del carrito por usuario
export const getItemsCarritoPorUsuario = (usuarioId) =>
  api.get(`/itemscarrito/usuario/${usuarioId}`);

// Actualizar cantidad de un item en el carrito
export const actualizarItemCarrito = (itemId, nuevaCantidad) =>
  api.put(`/itemscarrito/${itemId}/cantidad`, { quantity: nuevaCantidad });

// Eliminar item específico del carrito
export const eliminarItemCarrito = (itemId) => api.delete(`/itemscarrito/${itemId}`);

/* ======================================================
   GESTIÓN DE IMÁGENES CON CLOUDINARY
====================================================== */

// Configuración de Cloudinary para almacenamiento de imágenes
const CLOUDINARY_CLOUD_NAME = "dyjktrr3a";
const CLOUDINARY_UPLOAD_PRESET = "hazellab";

// Subir imagen a Cloudinary y obtener URL segura
export const subirImagen = async (archivo) => {
  if (!CLOUDINARY_CLOUD_NAME || !CLOUDINARY_UPLOAD_PRESET) {
    throw new Error("Configuración de Cloudinary incompleta");
  }

  if (!archivo || !archivo.type.startsWith("image/")) {
    throw new Error("Archivo no válido. Debe ser una imagen");
  }

  const formData = new FormData();
  formData.append("file", archivo);
  formData.append("upload_preset", CLOUDINARY_UPLOAD_PRESET);
  formData.append("folder", "hazellab/products");

  try {
    console.log("Iniciando subida de imagen a Cloudinary...", {
      cloudName: CLOUDINARY_CLOUD_NAME,
      preset: CLOUDINARY_UPLOAD_PRESET,
      file: { name: archivo.name, type: archivo.type, size: archivo.size },
    });

    const response = await fetch(
      `https://api.cloudinary.com/v1_1/${CLOUDINARY_CLOUD_NAME}/image/upload`,
      {
        method: "POST",
        body: formData,
      }
    );

    const data = await response.json();

    if (!response.ok) {
      console.error("Error en respuesta de Cloudinary:", data);
      throw new Error(data.error?.message || `Error ${response.status}: ${response.statusText}`);
    }

    console.log("Imagen subida exitosamente:", data.secure_url);
    return data.secure_url;
  } catch (error) {
    console.error("Error completo durante subida de imagen:", error);
    throw new Error(`No se pudo subir la imagen: ${error.message}`);
  }
};

// Probar conexión y configuración con Cloudinary
export const probarConexionCloudinary = async () => {
  const archivoDePrueba = new File(["test"], "test.png", { type: "image/png" });

  try {
    const url = await subirImagen(archivoDePrueba);
    return { success: true, url };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/* ======================================================
   UTILIDADES Y VALIDACIONES
====================================================== */

// Validar formato de RUT chileno
export const validarRUT = (rut) => {
  if (!rut) return false;
  
  const formatoValido = /^\d{1,2}\.\d{3}\.\d{3}-[\dkK]$/.test(rut);
  if (!formatoValido) return false;
  
  return true;
};

// Formatear RUT con puntos y guión
export const formatearRUT = (rut) => {
  if (!rut) return '';
  
  let rutLimpio = rut.replace(/[^0-9kK]/g, '');
  
  if (rutLimpio.length < 2) return rut;
  
  const numero = rutLimpio.slice(0, -1);
  const dv = rutLimpio.slice(-1).toUpperCase();
  
  return numero.replace(/\B(?=(\d{3})+(?!\d))/g, '.') + '-' + dv;
};

/* ======================================================
   GESTIÓN DE UBICACIONES GEOGRÁFICAS
====================================================== */

// Obtener lista de regiones de Chile
export const getRegiones = async () => {
  try {
    const response = await api.get('/ubicacion/regiones');
    return response;
  } catch (error) {
    console.error('Error al obtener regiones:', error);
    return { data: [
      "Arica y Parinacota", "Tarapacá", "Antofagasta", "Atacama", "Coquimbo",
      "Valparaíso", "Metropolitana", "O'Higgins", "Maule", "Ñuble",
      "Biobío", "Araucanía", "Los Ríos", "Los Lagos", "Aysén", "Magallanes"
    ] };
  }
};

// Obtener comunas por región específica
export const getComunasPorRegion = async (region) => {
  try {
    const response = await api.get(`/ubicacion/comunas/${encodeURIComponent(region)}`);
    return response;
  } catch (error) {
    console.error(`Error al obtener comunas para ${region}:`, error);
    return { data: [] };
  }
};

// Obtener estructura completa de regiones y comunas
export const getRegionesComunas = () => {
  return api.get('/api/ubicacion/regiones-comunas');
};

/* ======================================================
   ESTADÍSTICAS Y REPORTES
====================================================== */

// Obtener estadísticas generales del dashboard
export const getEstadisticasDashboard = () => {
  return api.get('/api/dashboard/estadisticas');
};

// Búsqueda avanzada de productos con múltiples filtros
export const buscarProductosAvanzado = (filtros) => {
  const params = new URLSearchParams();
  
  if (filtros.nombre) params.append('nombre', filtros.nombre);
  if (filtros.categoriaId) params.append('categoriaId', filtros.categoriaId);
  if (filtros.activo !== undefined) params.append('activo', filtros.activo);
  if (filtros.stockBajo) params.append('stockBajo', true);
  if (filtros.destacado !== undefined) params.append('destacado', filtros.destacado);
  if (filtros.precioMin) params.append('precioMin', filtros.precioMin);
  if (filtros.precioMax) params.append('precioMax', filtros.precioMax);
  
  return api.get(`/api/productos/buscar/avanzada?${params.toString()}`);
};

// Búsqueda avanzada de usuarios con filtros específicos
export const buscarUsuariosAvanzado = (filtros) => {
  const params = new URLSearchParams();
  
  if (filtros.username) params.append('username', filtros.username);
  if (filtros.email) params.append('email', filtros.email);
  if (filtros.rol) params.append('rol', filtros.rol);
  if (filtros.estado) params.append('estado', filtros.estado);
  if (filtros.region) params.append('region', filtros.region);
  
  return api.get(`/api/usuarios/buscar/avanzada?${params.toString()}`);
};

// Obtener ranking de productos más vendidos
export const getProductosMasVendidos = () => {
  return api.get('/api/dashboard/productos-mas-vendidos');
};