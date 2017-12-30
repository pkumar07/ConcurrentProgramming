# ThreadLocal

Implementation of ThreadLocal class in C++

Thread local variables are provided in Java via the java.lang.ThreadLocal<T> class and in C++11 via the thread_local storage class specifier. They provide a convenient way for each thread to have its own independently initialized and maintained instance of an object.

Each variable encapsulates a map from thread to value, and the interface and behavior is similar to Java’s, except that it does not have an initial_value method — each thread will be responsible for initializing their instance by calling set. Calling get prior to calling set will result in an exception being thrown.

More information on ThreadLocal class: 
https://docs.oracle.com/javase/8/docs/api/java/lang/ThreadLocal.html 
http://en.cppreference.com/w/cpp/language/storage_duration
