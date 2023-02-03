package com.example.librarymanagement

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.librarymanagement.database.LibraryViewModel
import com.example.librarymanagement.database.livedata.observeAsState
import com.example.librarymanagement.database.model.Book
import com.example.librarymanagement.ui.theme.LibraryManagementTheme
import com.example.librarymanagement.utils.Utils.resizeImage
import java.io.ByteArrayOutputStream


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LibraryManagementTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LibraryScreen(libraryViewModel = LibraryViewModel(application))

                }
            }
        }
    }
}

@Composable
fun LibraryScreen(libraryViewModel: LibraryViewModel) {
    val books by libraryViewModel.allBooks.observeAsState(mutableListOf())
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val bitmap = remember {
        mutableStateOf<Bitmap?>(null)
    }
    val imagePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                bitmap.value = ImageDecoder.decodeBitmap(source)
            }
        }
    val checkPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.READ_MEDIA_IMAGES
                )
            } else {
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    }
    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                items(books) { book ->
                    BookCard(book = book, libraryViewModel, checkPermission)
                }
            }

        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            FloatingActionButton(
                onClick = { showDialog.value = true },
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(8.dp)
                    .wrapContentSize()
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
            AddBookDialog(
                openDialogCustom = showDialog,
                libraryViewModel,
                bitmap,
                checkPermission
            )
        }


    }
}


@Composable
fun BookCard(
    book: Book,
    libraryViewModel: LibraryViewModel,
    checkPermission: ManagedActivityResultLauncher<String, Boolean>
) {
    val showEditDialog = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    Card(
        elevation = 8.dp, modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp), verticalArrangement = Arrangement.SpaceEvenly
        ) {
            book.coverImage?.let { btm ->
                val bitmapImage = BitmapFactory.decodeByteArray(btm, 0, btm.size)
                Image(
                    bitmap = bitmapImage.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .wrapContentSize()
                        .size(100.dp)
                        .clip(RectangleShape)
                )
            }
            Text("Title: ${book.title}", modifier = Modifier.padding(top = 8.dp))
            Text("Author: ${book.author}", modifier = Modifier.padding(top = 8.dp))
        }
        Column(Modifier.padding(top = 8.dp, bottom = 8.dp), horizontalAlignment = Alignment.End) {
            IconButton(onClick = {
                showEditDialog.value = true
            }) {
                Icon(Icons.Filled.Edit, contentDescription = null)
            }

            IconButton(onClick = { libraryViewModel.deleteBook(book) }) {
                Icon(Icons.Filled.Delete, contentDescription = null)
            }
            UpdateBookDialog(
                showDialog = showEditDialog,
                libraryViewModel = libraryViewModel,
                book,
                checkPermission
            )
        }
    }
}

@Composable
fun UpdateBookDialog(
    showDialog: MutableState<Boolean>,
    libraryViewModel: LibraryViewModel,
    book: Book,
    checkPermission: ManagedActivityResultLauncher<String, Boolean>
) {
    val openDialog = remember { showDialog }
    var title by remember { mutableStateOf(book.title) }
    var author by remember { mutableStateOf(book.author) }
    var imageByteArray = book.coverImage

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(
                    text = "Title",
                    Modifier.padding(top = 12.dp, bottom = 12.dp),
                    color = if (isSystemInDarkTheme()) {
                        Color.White
                    } else {
                        Color.Black
                    }
                )
            },
            text = {
                Column() {
                    TextField(
                        value = title,
                        onValueChange = { title = it }
                    )
                    Text(
                        text = "Author",
                        color = if (isSystemInDarkTheme()) {
                            Color.White
                        } else {
                            Color.Black
                        },
                        modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
                    )
                    TextField(
                        value = author,
                        onValueChange = { author = it }
                    )
                    if (imageByteArray == null) {
                        Image(painter = painterResource(id = R.drawable.ic_no_image),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(top = 12.dp, bottom = 12.dp)
                                .wrapContentSize()
                                .size(100.dp)
                                .clip(RectangleShape)
                                .clickable {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        checkPermission.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                                    } else {
                                        checkPermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                    }
                                })
                    } else {
                        imageByteArray?.let { btm ->
                            val convertedImage = BitmapFactory.decodeByteArray(btm, 0, btm.size)
                            Image(
                                bitmap = convertedImage.asImageBitmap(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .wrapContentSize()
                                    .size(100.dp)
                                    .clip(RectangleShape)
                                    .clickable {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            checkPermission.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                                        } else {
                                            checkPermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                        }
                                    })
                        }
                    }
                }
            },
            buttons = {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val updatedBook =
                                Book(title = title, author = author, coverImage = imageByteArray)
                            libraryViewModel.updateBook(updatedBook)
                            openDialog.value = false
                        }
                    ) {
                        Text("Add Book")
                    }
                }
            }
        )
    }
}

@Composable
fun AddBookDialog(
    openDialogCustom: MutableState<Boolean>,
    libraryViewModel: LibraryViewModel,
    bitmap: MutableState<Bitmap?>,
    imagePickerLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    val openDialog = remember { openDialogCustom }
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var imageByteArray = byteArrayOf()

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(
                    text = "Title",
                    Modifier.padding(top = 12.dp, bottom = 12.dp),
                    color = if (isSystemInDarkTheme()) {
                        Color.White
                    } else {
                        Color.Black
                    }
                )
            },
            text = {
                Column() {
                    TextField(
                        value = title,
                        onValueChange = { title = it }
                    )
                    Text(
                        text = "Author",
                        color = if (isSystemInDarkTheme()) {
                            Color.White
                        } else {
                            Color.Black
                        },
                        modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
                    )
                    TextField(
                        value = author,
                        onValueChange = { author = it }
                    )
                    if (bitmap.value == null) {
                        Image(painter = painterResource(id = R.drawable.ic_no_image),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(top = 12.dp, bottom = 12.dp)
                                .wrapContentSize()
                                .size(100.dp)
                                .clip(RectangleShape)
                                .clickable {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        imagePickerLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                                    } else {
                                        imagePickerLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                    }
                                })
                    } else {
                        bitmap.value?.let { btm ->
                            Image(
                                bitmap = btm.asImageBitmap(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .wrapContentSize()
                                    .size(100.dp)
                                    .clip(RectangleShape)
                                    .clickable {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            imagePickerLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                                        } else {
                                            imagePickerLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                        }
                                    })

                            val stream = ByteArrayOutputStream()
                            resizeImage(btm).compress(Bitmap.CompressFormat.PNG, 90, stream)
                            imageByteArray = stream.toByteArray()
                        }
                    }
                }
            },
            buttons = {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val book =
                                Book(title = title, author = author, coverImage = imageByteArray)
                            libraryViewModel.addBook(book)
                            bitmap.value = null
                            title = ""
                            author = ""
                            openDialog.value = false
                        }
                    ) {
                        Text("Add Book")
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LibraryManagementTheme {
        LibraryScreen(libraryViewModel = LibraryViewModel(Application()))
    }
}