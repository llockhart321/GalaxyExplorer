GalExplorer is a space sim where players pilot a spacecraft through a procedurally generated spiral galaxy with star systems, destructible asteroids, orbital mechanics, and jump gate networks.
Designed from the ground up using object-oriented design patterns, GalExplorer emphasizes performance, scalability, and immersion.

Key Features:
  1. Procedural Galaxy Generation
     - Spiral Structure Modeling using exponential equations
     - Chunk-Based World Generation for memory-efficient exploration
     - Persistent Galaxy regeneration from saved state
     - Density Gradients: denser star populations near the galactic core
      
  2. Navigation & Physics
     - Momentum-Based Flight with acceleration and deceleration
     - Collision Detection for all celestial objects
     - Orbital Mechanics for planets and asteroid belts
     - Smooth Camera Tracking with visual bounds

 3. Star System Content
    - 1–10 Planets per System with procedural attributes
    - Destructible Asteroid Belts
    - Directional Jump Gates linking star systems
    - Parallax Nebulae and Particle Effects
      
 4. Map Systems
    - Zoomable Galaxy Map with system visualization
    - Mini Map for local system navigation
    - Visited System Tracking
    
 5. Combat & Hazards
    - Point-and-Click Missiles with physics-based collisions
    - Environmental Hazards and destructible objects
    - Debug Mode reveals full galaxy structure (80,000+ systems)
    
Architecture & Design Patterns:
  1. Singletons:Camera, Player, GalaxyMap, etc.
  2. State Pattern: For player movement behavior
  3. Factory Pattern: Star system instantiation and caching
  4. Iterator Pattern: System traversal and rendering

Project Structure
  - Main.java                   # Game entry point
  - GalaxyMap.java              # Spiral generation, map UI
  - StarSystem.java             # System logic & orbit dynamics
  - Planet.java/Asteroid.java   # Celestial body mechanics
  - MissileSystem.java          # Combat & missile physics
  - Camera.java                 # Viewport & movement tracking
  - galaxy_data/systems.txt     # Persistent galaxy save

Controls
  - WASD/Arrow Keys: Move
  - Mouse Click: Fire missiles
  - M: Galaxy map
  - N: Mini map
  - X: Debug mode

Installation:
  1. Prerequisites:
      - Java 11+
      - JavaFX SDK
  2. Run:
     - javac *.java
     - java Main
    
Optimization & Extendability
  - View Culling for efficient rendering
  - Chunk-Based Spawning for infinite world feel
  - Modular Design: Easily extend planets, factions, and mission systems  

Summary for Recruiters
  - GalExplorer demonstrates:
    1. Strong Java OOP architecture
    2. Procedural map generation & chunk loading
    3. Physics systems, rendering pipelines, and data persistence
    4. Application of design patterns and performance-minded development
