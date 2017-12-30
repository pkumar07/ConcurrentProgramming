/*
* threadLocal.h
*  Start with this and add what is necessary
*/

//using namespace std;

#ifndef THREADLOCAL_H_
#define THREADLOCAL_H_

#include<iostream>
#include<thread>
#include<mutex>
#include<unordered_map>



namespace cop5618 {


	template <typename T>
	class threadLocal {
	public:
		threadLocal();
		~threadLocal();

		//disable copy, assign, move, and move assign constructors
		threadLocal(const threadLocal&) = delete;
		threadLocal& operator=(const threadLocal&) = delete;
		threadLocal(threadLocal&&) = delete;
		threadLocal& operator=(const threadLocal&&) = delete;

		/**
		* Returns the current thread's value.
		* If no value has been previously set by this
		* thread, an out_of_range exception is thrown.
		*/


		const T& get() const;
		/**
		* Sets the value of the threadLocal for the current thread
		* to val.
		*/
		void set(T val);
		/**
		* Removes the current thread's value for the threadLocal
		*/
		void remove();

		/**
		* Friend function.  Useful for debugging only
		*/

		template <typename U>
		friend std::ostream& operator<< (std::ostream& os, const threadLocal<U>& obj);

	private:
		//ADD PRIVATE MEMBERS

		mutable std::mutex m;
		std::unordered_map<std::thread::id, T> unordered_threadmap;
		




	};

	template<typename T>
	threadLocal<T>::threadLocal() {	}

	template<typename T>
	threadLocal<T>::~threadLocal() {	}

	template<typename T>
	const T & threadLocal<T>::get() const
	{

		std::lock_guard<std::mutex> lock(m);

		std::thread::id tid = std::this_thread::get_id();
		std::unordered_map<std::thread::id, T>::const_iterator findthreadid = unordered_threadmap.find(tid);

		if (findthreadid == unordered_threadmap.end())
			throw "No_such_value \n";

		
		std::cout << "get_value_of_thread: " << tid << " " << findthreadid->second << "\n";
		return findthreadid->second;


	}

	
	template<typename T>
	void threadLocal<T>::set(T val)
	{
		std::lock_guard<std::mutex> lock(m);
		std::thread::id tid = std::this_thread::get_id();
		unordered_threadmap[tid] = val;

	}


	template<typename T>
	void threadLocal<T>::remove()
	{
		std::lock_guard<std::mutex> lock(m);
		std::thread::id tid = std::this_thread::get_id();
		std::unordered_map<std::thread::id, T>::const_iterator findthreadid = unordered_threadmap.find(tid);

		if (findthreadid == unordered_threadmap.end()) {
			throw "Value not found";
		}

		unordered_threadmap.erase(findthreadid);
		
	}




} /* namespace cop5618 */

#endif /* THREADLOCAL_H_ */

