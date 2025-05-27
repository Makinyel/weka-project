# 🧠 Sistema Predictivo de Aprobación de Préstamos

Este proyecto consiste en una *aplicación web* que predice la *aprobación* y *monto* de préstamos utilizando *machine learning con Weka, un backend desarrollado en **Java con Spring Boot, y un frontend moderno en **React con TypeScript*.

## 🚀 Funcionalidades

- Predicción del estado de una solicitud de préstamo: Aprobado o Rechazado.
- Estimación del monto probable a aprobar.
- Interfaz intuitiva y responsiva.
- Backend y modelos integrados y desplegados en la nube.

## 🛠️ Tecnologías utilizadas

### ⚙️ Backend

- *Java + Spring Boot*
    - API REST para recibir datos y retornar predicciones.
    - Integración de modelos .model generados con Weka.
- *Weka*
    - *J48* (árbol de decisión) para clasificación.
    - *Regresión lineal* para estimación del monto.
- *Docker*
    - Contenerización del backend para facilitar despliegue.
- *Render.com*
    - Plataforma donde se despliega el backend.

### 💻 Frontend

- *React + TypeScript*
    - Componente LoanForm para enviar datos al backend.
    - PredictionModal y AmountModal para mostrar resultados.
    - Estado dinámico usando useState.
- *Tailwind CSS*
    - Estilizado rápido y adaptativo.
- *shadcn/ui*
    - Componentes reutilizables y modernos.
- *lucide-react*
    - Íconos SVG personalizados.
- *Vercel*
    - Despliegue del frontend con integración CI/CD desde GitHub.

## 📊 Modelos de Machine Learning

Entrenados y evaluados con *Weka*, usando datasets públicos relacionados con aprobación de préstamos:

- *J48*: Árbol de decisión para clasificación del préstamo (Aprobado/Rechazado).
- *Regresión Lineal*: Para predecir el monto del préstamo a otorgar.

Los modelos son exportados como archivos .model y utilizados desde el backend Java.

## 🧪 Validación

- Pruebas funcionales del sistema.
- Análisis de precisión de los modelos (validación cruzada en Weka).
- Simulación de solicitudes reales para verificar desempeño.

## 📂 Estructura del repositorio

```bash
.
├── backend/
│   ├── src/
│   ├── Dockerfile
│   ├── modelos/
│   │   ├── modelo_aprobacion.model
│   │   └── modelo_monto.model
│   └── ...
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   └── pages/
│   └── ...
├── README.md
└── ...