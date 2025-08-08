package com.manage.security.models;

import java.util.Objects;

public class ZExampleEqualsHashCodeModel {

    private Long id;
    private String username;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    // Equals y hash personalizados (Versión moderna) - en HashSet<>, evita duplicados, al verificar primero
    // por el hash y si existe colisión (el resultado es el mismo), compara con el equals, para terminar de
    // comparar si es el mismo objeto
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZExampleEqualsHashCodeModel user = (ZExampleEqualsHashCodeModel) o;
        return Objects.equals(id, user.id) && 
            Objects.equals(username, user.username);
    }
    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.username);
    }

    // // MÉTODOS SOBREESCRITO POR IDE (TRADICIONALES)
    // @Override
    // public boolean equals(Object obj) {
    //     if (this == obj)
    //         return true;
    //     if (obj == null)
    //         return false;
    //     if (getClass() != obj.getClass())
    //         return false;
    //     User other = (User) obj;
    //     if (id == null) {
    //         if (other.id != null)
    //             return false;
    //     } else if (!id.equals(other.id))
    //         return false;
    //     if (username == null) {
    //         if (other.username != null)
    //             return false;
    //     } else if (!username.equals(other.username))
    //         return false;
    //     return true;
    // }
    // // Utiliza 31 para algorítmo de número impar y primo, evita colisiones de hash
    // @Override
    // public int hashCode() {
    //     final int prime = 31;
    //     int result = 1;
    //     result = prime * result + ((id == null) ? 0 : id.hashCode());
    //     result = prime * result + ((username == null) ? 0 : username.hashCode());
    //     return result;
    // }

    //     1. Orden de Comparación en equals()
    // ✅ Por qué primero se verifica la instancia (this == obj) antes de null:
    // Razón 1: Un objeto nunca es igual a null, pero sí es igual a sí mismo. Esta comparación es rápida y evita accesos innecesarios a memoria.
    // Razón 2: Si obj es null, la condición this == obj será false y luego se rechazará en el siguiente if (obj == null).
    // 🔹 Ejemplo:
    // java
    // @Override
    // public boolean equals(Object obj) {
    //     if (this == obj) return true;  // Optimización: verifica si es la misma instancia
    //     if (obj == null) return false; // Luego verifica null
    //     // ... resto de la lógica
    // }

    // 2. Instancia vs Hash vs Valor
    // Concepto	Descripción	Ejemplo
    // Instancia	Referencia única en memoria asignada por Java (dirección hexadecimal como User@5b2133b1). No cambia durante la vida del objeto.	user1 == user2 (compara direcciones).
    // Hash	Número calculado (con hashCode()) basado en los campos del objeto. Puede repetirse (colisión).	user1.hashCode() → 42.
    // Valor	Contenido de los campos del objeto (comparado en equals()).	user1.getName().equals(user2.getName()).
    // ✅ Clave:
    // Instancia: Identidad física en memoria.
    // Hash: Representación numérica del estado (para estructuras como HashMap).
    // Valor: Contenido lógico del objeto.

    // 4. Caso Práctico: Hash Colisionado
    // Si dos objetos tienen todos sus campos null, su hash será 961 (como vimos antes), pero equals() los diferenciará si es necesario:
    // java
    // User userA = new User(null, null);
    // User userB = new User(null, null);
    // System.out.println(userA.hashCode()); // 961
    // System.out.println(userB.hashCode()); // 961
    // System.out.println(userA.equals(userB)); // true (todos los campos coinciden)

    // RECOMENDADO IMPLEMENTARLO EN CLASES DE MODELOS QUE IMPLEMENTAN LÓGICA DE APLICACIÓN (CON EL ID)
    // 1. ¿Deben implementarse equals() y hashCode() en entidades/modelos?
    // ✅ Sí, es altamente recomendado, especialmente si:
    // Usas colecciones como HashSet, HashMap o operaciones de búsqueda/eliminación.
    // Necesitas comparar lógicamente objetos (ej.: verificar si dos User son el mismo).
    // ⚠️ Si no los implementas:
    // Java usará la implementación por defecto de Object:
    // equals(): Compara instancias (direcciones de memoria), no contenido.
    // hashCode(): Devuelve un número basado en la dirección de memoria (no en campos).
    // 🔹 Ejemplo de problema sin implementación:
    // java
    // User user1 = new User(1L, "john");  // Mismo ID y username
    // User user2 = new User(1L, "john");
    // System.out.println(user1.equals(user2));  // false (¡Error! Debería ser true)
    // Set<User> users = new HashSet<>();
    // users.add(user1);
    // users.add(user2);  // Se agregará como duplicado (hashCode diferente)

}
