#include <jni.h>
#include <string>

extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_example_testapp_MainActivity_getPhonebook(JNIEnv *env, jobject)
{
    srand(time(nullptr));

    // будет сгенерировано между 5 и 20 парами имя-номер
    int num_entries = rand() % 15 + 5;

    jclass phClass = env->FindClass("com/example/testapp/PhonebookEntry");
    jobjectArray phArray = env->NewObjectArray(num_entries, phClass, nullptr);
    jmethodID phInit = env->GetMethodID(phClass, "<init>",
                                        "(Ljava/lang/String;Ljava/lang/String;)V");

    for (int j = 0; j < num_entries; ++j)
    {
        std::string name, number;

        // генерирует фамилию - рандомный набор букв от 5 до 8 символов
        name += rand() % 26 + 'A';
        int name_length = rand() % 3 + 4;
        for (int i = 0; i < name_length; ++i)
            name += rand() % 26 + 'a';

        // генерирует номер - рандомный набор цифр
        for (int i = 0; i < 11; ++i)
            number += rand() % 10 + '0';

        jobject phObj = env->NewObject(phClass, phInit,
                                       env->NewStringUTF(name.c_str()),
                                       env->NewStringUTF(number.c_str()));
        env->SetObjectArrayElement(phArray, j, phObj);
    }

    return phArray;
}