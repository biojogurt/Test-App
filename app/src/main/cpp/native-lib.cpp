#include <jni.h>
#include <string>

extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_example_testapp_MainActivity_getPhonebook(JNIEnv *env, jobject)
{
    srand(time(nullptr));
    jclass phClass = env->FindClass("com/example/testapp/PhonebookEntry");

    // будет сгенерировано между 20 и 35 парами имя-номер
    int num_entries = rand() % 15 + 20;
    jobjectArray phArray = env->NewObjectArray(num_entries, phClass, nullptr);

    for (int j = 0; j < num_entries; ++j)
    {
        std::string name, number;

        // генерирует фамилию - рандомный набор букв от 8 до 12 символов
        name += static_cast<char> (rand() % 26 + 'A');
        int name_length = rand() % 4 + 7;
        for (int i = 0; i < name_length; ++i)
            name += static_cast<char> (rand() % 26 + 'a');

        // генерирует номер - рандомный набор цифр
        for (int i = 0; i < 11; ++i)
            number += static_cast<char> (rand() % 10 + '0');

        env->SetObjectArrayElement(phArray, j, env->NewObject(phClass,
                                                              env->GetMethodID(phClass, "<init>",
                                                                               "(Ljava/lang/String;Ljava/lang/String;)V"),
                                                              env->NewStringUTF(name.c_str()),
                                                              env->NewStringUTF(number.c_str())));
    }

    return phArray;
}