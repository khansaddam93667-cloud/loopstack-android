with open('./app/src/main/java/com/loopstack/presentation/herocard/HeroCard.kt', 'r') as f:
    content = f.read()

# Replace active color
content = content.replace('Color(0xFF00FF66)', 'Color(0xFF4CAF50)')

# Replace inactive/degraded color
content = content.replace('Color(0xFFFFB300)', 'Color(0xFFFFC107)')

with open('./app/src/main/java/com/loopstack/presentation/herocard/HeroCard.kt', 'w') as f:
    f.write(content)
