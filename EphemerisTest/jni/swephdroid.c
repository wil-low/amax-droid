#include "swephexp.h"
#include <jni.h>
#include <android/log.h>

#define CALC_UT_ARRAY_SIZE 6
#define HOUSE_ARRAY_SIZE 23
#define ECLIPSE_ARRAY_SIZE 10

char msg_buf[256];

void
Java_com_astromaximum_util_SwissEphemeris_setEphePath(
	JNIEnv*  env, jobject this,
	jstring path)
{
	const jbyte *str;
	str = (*env)->GetStringUTFChars(env, path, NULL);
	if (str == NULL) {
		return; /* OutOfMemoryError already thrown */
	}
	swe_set_ephe_path((char*)str);
	(*env)->ReleaseStringUTFChars(env, path, str);
}

void
Java_com_astromaximum_util_SwissEphemeris_close(
	JNIEnv*  env, jobject this)
{
	swe_close();
}

jstring
Java_com_astromaximum_util_SwissEphemeris_getPlanetName(
	JNIEnv*  env, jobject this,
	jint planetId)
{
	swe_get_planet_name (planetId, msg_buf);
	return (*env)->NewStringUTF(env, msg_buf);
}

jdouble
Java_com_astromaximum_util_SwissEphemeris_getJulday(
	JNIEnv*  env, jobject this,
    jint year, jint month, jint day, jdouble hour, jboolean gregFlag)
{
	return swe_julday (year, month, day, hour, gregFlag);
}

jdoubleArray
Java_com_astromaximum_util_SwissEphemeris_calcUT(
	JNIEnv*  env, jobject this,
    jdouble juldayUT, jint planetId, jint flag)
{
	double lon_lat_rad[CALC_UT_ARRAY_SIZE];
	jint ret_flag = swe_calc_ut (juldayUT, planetId, SEFLG_SPEED, lon_lat_rad, msg_buf);
	jdoubleArray ret = (*env)->NewDoubleArray (env, CALC_UT_ARRAY_SIZE);
	__android_log_print (ANDROID_LOG_DEFAULT, "calcUT", "Error %d: %s", ret_flag, msg_buf);
	if (ret_flag != 256)
		__android_log_print (ANDROID_LOG_FATAL, "calcUT", "Error %d: %s", ret_flag, msg_buf);
	(*env)->SetDoubleArrayRegion (env, ret, 0, CALC_UT_ARRAY_SIZE, lon_lat_rad);
	return ret;
}

jdoubleArray
Java_com_astromaximum_util_SwissEphemeris_calcHouses(
	JNIEnv*  env, jobject this,
	jdouble juldayUT, jdouble geoLat, jdouble geoLon, jchar houseSystem)
{
	double cusps_acsmc[HOUSE_ARRAY_SIZE];
	jint ret_flag = swe_houses (juldayUT, geoLat, geoLon, houseSystem, cusps_acsmc, cusps_acsmc + 13);
	jdoubleArray ret = (*env)->NewDoubleArray (env, HOUSE_ARRAY_SIZE);
	(*env)->SetDoubleArrayRegion (env, ret, 0, HOUSE_ARRAY_SIZE, cusps_acsmc);
	return ret;
}

jdoubleArray
Java_com_astromaximum_util_SwissEphemeris_solEclipseWhenGlob(
	JNIEnv*  env, jobject this,
    jdouble juldayUTStart, jint ephFlag, jint eclipseType, jboolean isBackwardSearch)
{
	double eclipse[ECLIPSE_ARRAY_SIZE];
	jint ret_flag = swe_sol_eclipse_when_glob (juldayUTStart, ephFlag, eclipseType, eclipse, 
		isBackwardSearch, msg_buf);
	jdoubleArray ret = (*env)->NewDoubleArray (env, ECLIPSE_ARRAY_SIZE);
	__android_log_print (ANDROID_LOG_DEFAULT, "solEclipseWhenGlob", "Error %d: %s", ret_flag, msg_buf);
	if (ret_flag != 256)
		__android_log_print (ANDROID_LOG_FATAL, "solEclipseWhenGlob", 
		"Error %d: %s", ret_flag, msg_buf);
	(*env)->SetDoubleArrayRegion (env, ret, 0, ECLIPSE_ARRAY_SIZE, eclipse);
	return ret;
}