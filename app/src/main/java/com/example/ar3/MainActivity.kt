package com.example.ar3

import android.content.res.ColorStateList
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.animation.ModelAnimator
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var anchorNode: AnchorNode? = null
    private var animator: ModelAnimator? = null
    private var nextAnimation: Int = 0
    private var animationCrap: ModelRenderable? = null
    private var transformNode: TransformableNode? = null
    private var arFragment: ArFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        onClick()

        setUpModel()
    }

    private fun init() {
        arFragment = supportFragmentManager.findFragmentById(R.id.fragment) as ArFragment

        animate.isEnabled = false

    }

    private fun onClick() {
        animate.setOnClickListener {

            if (animator == null || !animator!!.isRunning) {

                val data = animationCrap!!.getAnimationData(nextAnimation)
                nextAnimation = (nextAnimation + 1) % animationCrap!!.animationDataCount
                animator = ModelAnimator(data, animationCrap)
                animator!!.start()
                val mp = MediaPlayer.create(this@MainActivity, R.raw.pushsound)
                mp.start()

            }

        }
        arFragment!!.setOnTapArPlaneListener { hitResult, plane, motionEvent ->

            if (animationCrap != null) {
                val anchor = hitResult.createAnchor()

                if (anchorNode == null) {
                    anchorNode = AnchorNode(anchor)
                    anchorNode!!.setParent(arFragment!!.arSceneView.scene)


                    transformNode = TransformableNode(arFragment!!.transformationSystem)
                    transformNode!!.scaleController.minScale = 0.09f
                    transformNode!!.scaleController.maxScale = 0.1f
                    transformNode!!.setParent(anchorNode)
                    transformNode!!.renderable = animationCrap
                }
            }


        }


        arFragment!!.arSceneView.scene.addOnUpdateListener {


            if (anchorNode == null) {

                if (animate.isEnabled) {

                    animate.backgroundTintList = ColorStateList.valueOf(android.graphics.Color.GRAY)
                    animate.isEnabled = false
                }
            } else {

                if (!animate.isEnabled) {

                    animate.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.colorAccent
                        )
                    )
                    animate.isEnabled = true
                }

            }


        }

    }

    private fun setUpModel() {
        ModelRenderable.builder()
            .setSource(this, R.raw.model_fight)
            .build().thenAccept { modelRenderable -> animationCrap = modelRenderable }
            .exceptionally { throwable ->

                Toast.makeText(this@MainActivity, "" + throwable.message, Toast.LENGTH_LONG).show()
                null
            }

    }
}
