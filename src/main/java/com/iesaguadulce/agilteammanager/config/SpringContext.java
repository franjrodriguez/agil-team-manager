package com.iesaguadulce.agilteammanager.config;

import org.springframework.context.ApplicationContext;

/**
 * Clase utilitaria para proporcionar acceso estático al contexto de Spring
 * {@link ApplicationContext} en toda la aplicación.
 *
 * <p>Esta clase actúa como un wrapper estático que permite obtener beans de Spring
 * desde cualquier lugar del código, incluso en clases que no son gestionadas por el
 * contenedor de Spring.</p>
 *
 * <p><strong>Nota de uso:</strong> Esta clase debe ser inicializada llamando a
 * {@link #setApplicationContext(ApplicationContext)} durante el arranque de la aplicación,
 * típicamente desde un {@link org.springframework.context.ApplicationContextAware} o
 * un listener de contexto.</p>
 *
 * <p><strong>Ejemplo de inicialización:</strong></p>
 * <pre>
 * &#64;Component
 * public class SpringContextInitializer implements ApplicationContextAware {
 *     &#64;Override
 *     public void setApplicationContext(ApplicationContext context) {
 *         SpringContext.setApplicationContext(context);
 *     }
 * }
 * </pre>
 *
 * <p><strong>Ejemplo de uso:</strong></p>
 * <pre>
 * MiServicio servicio = SpringContext.getBean(MiServicio.class);
 * </pre>
 *
 * @author Francisco José Rodríguez Ruiz
 * @version 1.0
 * @since 1.0
 * @see org.springframework.context.ApplicationContext
 */

public class SpringContext {

    /**
     * Contexto de aplicación de Spring almacenado de forma estática.
     * Se inicializa mediante {@link #setApplicationContext(ApplicationContext)}.
     */
    private static ApplicationContext context;

    /**
     * Establece el contexto de aplicación de Spring.
     *
     * <p>Este método debe ser llamado una sola vez durante la inicialización
     * de la aplicación, típicamente desde un componente que implemente
     * {@link org.springframework.context.ApplicationContextAware}.</p>
     *
     * <p><strong>Advertencia:</strong> Si se llama múltiples veces, el contexto
     * anterior será sobrescrito.</p>
     *
     * @param applicationContext el contexto de aplicación de Spring a almacenar;
     *                          no debe ser {@code null}
     * @throws IllegalArgumentException si {@code applicationContext} es {@code null}
     */
    public static void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    /**
     * Obtiene un bean del tipo especificado desde el contexto de Spring.
     *
     * <p>Busca y devuelve una instancia del bean del tipo solicitado. Si existen
     * múltiples beans del mismo tipo, se lanzará una excepción.</p>
     *
     * <p>Este método permite acceder a los beans gestionados por Spring desde
     * clases que no son componentes de Spring.</p>
     *
     * @param <T> el tipo del bean a obtener
     * @param beanClass la clase del bean que se desea obtener; no debe ser {@code null}
     * @return una instancia del bean del tipo especificado
     * @throws IllegalStateException si el contexto no ha sido inicializado
     * @throws org.springframework.beans.BeansException si no se encuentra el bean,
     *         hay múltiples candidatos, o hay algún otro error de Spring
     * @see org.springframework.context.ApplicationContext#getBean(Class)
     */
    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
}
