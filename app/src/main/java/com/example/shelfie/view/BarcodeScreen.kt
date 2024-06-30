import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.shelfie.ui.theme.DarkPurple
import com.example.shelfie.ui.theme.LightPurple
import com.example.shelfie.view.BottomNavigationBar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BarcodeScreen(navController: NavController) {
    var hasCameraPermission by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
    androidx.compose.material3.Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            IconButton(
                onClick = { navController.popBackStack()},
                modifier = Modifier.padding(10.dp, 20.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack, contentDescription = "Back",
                    tint = LightPurple)
            }
            Box(modifier = Modifier.fillMaxSize()) {
                if (hasCameraPermission) {
                    CameraPreviewView(navController = navController, lifecycleOwner = lifecycleOwner)
                } else {
                    Text("Camera permission is needed to scan barcodes.")
                }


            }
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun CameraPreviewView(navController: NavController, lifecycleOwner: LifecycleOwner) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        ) {
            AndroidView(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(),
                factory = { context ->
                    PreviewView(context).apply {
                        this.scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                }
            ) { previewView ->
                val cameraProviderFuture = ProcessCameraProvider.getInstance(previewView.context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val barcodeScanner = BarcodeScanning.getClient()
                    val imageAnalysis = ImageAnalysis.Builder().build().also {
                        it.setAnalyzer(ContextCompat.getMainExecutor(previewView.context)) { imageProxy ->
                            val mediaImage = imageProxy.image
                            if (mediaImage != null) {
                                val inputImage = InputImage.fromMediaImage(
                                    mediaImage,
                                    imageProxy.imageInfo.rotationDegrees
                                )
                                barcodeScanner.process(inputImage)
                                    .addOnSuccessListener { barcodes ->
                                        for (barcode in barcodes) {
                                            val rawValue = barcode.rawValue
                                            Log.e("BarcodeScreen", "$rawValue")
                                            if (rawValue != null && barcode.valueType == Barcode.TYPE_ISBN) {
                                                navController.navigate("bookDetails/${rawValue}")
                                            }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("BarcodeScreen", "Barcode scanning failed", e)
                                    }
                                    .addOnCompleteListener {
                                        imageProxy.close()
                                    }
                            }
                        }
                    }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, imageAnalysis
                        )
                    } catch (exc: Exception) {
                        Log.e("BarcodeScreen", "Use case binding failed", exc)
                    }
                }, ContextCompat.getMainExecutor(previewView.context))
            }
        }
    }
}
