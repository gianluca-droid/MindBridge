package com.mindbridge.app.ui.patient

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mindbridge.app.data.model.User
import com.mindbridge.app.data.repository.MockRepository
import com.mindbridge.app.ui.components.ExerciseCard
import com.mindbridge.app.ui.components.GradientHeader

@Composable
fun ExercisesScreen(user: User) {
    val exercises = MockRepository.getEserciziByPaziente(user.id)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { GradientHeader("I Miei Esercizi", "Assegnati dal tuo terapeuta") }

        items(exercises) { exercise ->
            ExerciseCard(
                exercise = exercise,
                onComplete = if (exercise.stato != com.mindbridge.app.data.model.ExerciseStatus.COMPLETATO) {
                    { /* Mock complete action */ }
                } else null,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        item { Spacer(Modifier.height(100.dp)) }
    }
}
