package com.example.shoppinglistapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoppinglistapp.ui.theme.ShoppingListAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListAppTheme {
                MaterialTheme{
                    ShoppingListApp()
                }
            }
        }
    }
}

data class ShoppingItem(
    val id: Int,
    var name: String,
    var quantity: Int,
    var isEditing : Boolean = false
)

@Composable
fun ShoppingListApp() {
    val sItems = remember { mutableStateListOf<ShoppingItem>() }
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }

    val context = LocalContext.current   //For toast message

    Column(modifier = Modifier.fillMaxSize(),verticalArrangement = Arrangement.SpaceBetween) {
        Button(onClick = {showDialog = true},
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(40.dp)){
            Text(text = "Add Item")
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            items(sItems) {
                item ->   //it -> ShoppingItem
                if (item.isEditing)
                    ShoppingItemEditor(item = item ,
                        onEditComplete = { editedName, editedQuantity ->
                        val editedItem = sItems.find{it.id == item.id}
                        editedItem?.let{
                            it.name = editedName
                            it.quantity = editedQuantity }
                        sItems.replaceAll { it.copy(isEditing = false) }
                    } )
                else
                    ShoppingListItemVisual(item = item,
                        onEditClick = {
                            sItems.replaceAll { it.copy(isEditing = it.id == item.id) }
                        },
                        onDeleteClick = {sItems -= item}
                    )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { showDialog = false },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(238, 233, 242),
                            contentColor = Color(99, 84, 140)
                        ),
                        border = BorderStroke(2.dp, Color(99, 84, 140))

                    ){
                        Text("Cancel")
                    }
                    Button(onClick = {
                        if(itemName.isNotBlank() && itemQuantity.isNotBlank()){
                            val newItem = ShoppingItem(id= sItems.size + 1, name = itemName, quantity = itemQuantity.toInt())
                            sItems.add(newItem)
                            showDialog = false
                            itemName = ""
                            itemQuantity = ""
                        }
                        else
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()

                    }) {
                        Text("Add")
                    }
                }
            },
            title = { Text("Add Shopping Item", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        singleLine = true,
                        label = { Text("Item Name") },
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                    )

                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() }) {
                                itemQuantity = it
                        }},
                        singleLine = true,
                        label = { Text("Quantity") },
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                    )
                }
            },
            shape = RoundedCornerShape(32.dp)
        )
    }
}

@Composable
fun ShoppingItemEditor(item : ShoppingItem, onEditComplete: (String, Int) -> Unit){
    var editedName by remember { mutableStateOf(item.name)  }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }
    var isEditing by remember { mutableStateOf(item.isEditing) }

    Row(modifier = Modifier.fillMaxWidth().background(Color(238, 233, 242)).padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column {
            BasicTextField(value = editedName,
                onValueChange = {editedName = it},
                singleLine = true,
                modifier = Modifier.padding(8.dp).wrapContentSize()
            )

            BasicTextField(value = editedQuantity,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }){
                        editedQuantity = it }
                },
                singleLine = true,
                modifier = Modifier.padding(8.dp).wrapContentSize()
            )
        }

        Button(onClick = {
            isEditing = false
            onEditComplete(editedName, editedQuantity.toIntOrNull() ?: 1) },
            modifier = Modifier.align(Alignment.CenterVertically)
        ){
            Text("Save")
        }
    }
}

@Composable
fun ShoppingListItemVisual(
    item: ShoppingItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
){
    Row(
        modifier = Modifier.padding(8.dp).fillMaxWidth().
        border(
            border= BorderStroke(2.dp, Color(0xFF7A6A7E)),
            shape = RoundedCornerShape(20)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Text(item.name,fontSize = 20.sp, modifier = Modifier.weight(1f).padding(start = 16.dp))

        //For quantity
        Box(
            modifier = Modifier
                .weight(0.25f)
                .padding(4.dp)
                .border(0.8.dp, Color.Gray, RoundedCornerShape(30))
        ) {
            Text(
                text = item.quantity.toString(),
                modifier = Modifier.align(Alignment.Center).padding(4.dp)
            )
        }

        Row(modifier = Modifier.padding(8.dp)) {
            IconButton(onClick = onEditClick) {
                Icon(imageVector = androidx.compose.material.icons.Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = androidx.compose.material.icons.Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
