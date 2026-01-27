import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindcard.data.model.Mindcard
import com.mindcard.ui.components.MindcardCard
import com.mindcard.ui.theme.MindCardColors
import com.mindcard.ui.theme.MindCardTypography

@Composable
fun HomeScreen(
    userName: String,
    mindcards: List<Mindcard>,
    onMindcardClick: (Mindcard) -> Unit,
    onAddClick: () -> Unit
) {
    val cardColors = listOf(
        MindCardColors.OldFlax,
        MindCardColors.RocketOrange,
        MindCardColors.TaskBlue
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MindCardColors.Primary,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar",
                    tint = MindCardColors.Onprimary
                )
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding) // 🔥 IMPORTANTE
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Column(modifier = Modifier.padding(vertical = 24.dp)) {
                    Text("Olá, $userName!", style = MindCardTypography.Heading2)
                    Text(
                        "Vamos estudar hoje?",
                        style = MindCardTypography.Body,
                        color = MindCardColors.MutedForeground
                    )
                }
            }

            itemsIndexed(mindcards) { index, mindcard ->
                MindcardCard(
                    title = mindcard.title,
                    backgroundColor = cardColors[index % cardColors.size],
                    icon = { Text("📚", fontSize = 24.sp) },
                    onClick = { onMindcardClick(mindcard) }
                )
            }
        }
    }
}
