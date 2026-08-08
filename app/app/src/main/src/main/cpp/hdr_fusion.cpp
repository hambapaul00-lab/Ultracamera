#include <jni.h>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/photo.hpp>
#include <vector>
#include <android/log.h>

#define LOG_TAG "HDRNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_ultracamera_engine_HDRNativeBridge_processHDRFusionNative(
        JNIEnv *env,
        jobject thiz,
        jlongArray matAddrArray,
        jlong resultMatAddr) {

    if (matAddrArray == nullptr || resultMatAddr == 0) return JNI_FALSE;

    jsize numImages = env->GetArrayLength(matAddrArray);
    if (numImages < 2) return JNI_FALSE;

    jlong *addresses = env->GetLongArrayElements(matAddrArray, nullptr);
    if (addresses == nullptr) return JNI_FALSE;

    std::vector<cv::Mat> inputImages;
    for (int i = 0; i < numImages; i++) {
        cv::Mat *pMat = reinterpret_cast<cv::Mat *>(addresses[i]);
        if (pMat != nullptr && !pMat->empty()) {
            inputImages.push_back(*pMat);
        }
    }

    if (inputImages.size() < 2) {
        env->ReleaseLongArrayElements(matAddrArray, addresses, JNI_ABORT);
        return JNI_FALSE;
    }

    try {
        std::vector<cv::Mat> alignedImages;
        cv::Ptr<cv::AlignMTB> aligner = cv::createAlignMTB(6, 4, true);
        aligner->process(inputImages, alignedImages);

        cv::Mat fusion32F;
        cv::Ptr<cv::MergeMertens> mergeMertens = cv::createMergeMertens(1.0f, 1.0f, 1.0f);
        mergeMertens->process(alignedImages, fusion32F);

        cv::Mat fusion8U;
        fusion32F.convertTo(fusion8U, CV_8UC3, 255.0);

        cv::Mat labImage;
        cv::cvtColor(fusion8U, labImage, cv::COLOR_BGR2Lab);

        std::vector<cv::Mat> labChannels;
        cv::split(labImage, labChannels);

        cv::Ptr<cv::CLAHE> clahe = cv::createCLAHE(2.0, cv::Size(8, 8));
        clahe->apply(labChannels[0], labChannels[0]);

        cv::merge(labChannels, labImage);

        cv::Mat &outputMat = *(reinterpret_cast<cv::Mat *>(resultMatAddr));
        cv::cvtColor(labImage, outputMat, cv::COLOR_Lab2BGR);

        env->ReleaseLongArrayElements(matAddrArray, addresses, JNI_ABORT);
        return JNI_TRUE;

    } catch (...) {
        env->ReleaseLongArrayElements(matAddrArray, addresses, JNI_ABORT);
        return JNI_FALSE;
    }
}
