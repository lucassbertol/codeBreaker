// ...existing code...

  stop(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
      this.intervalId = null;
    }
    this.isRunning = false;
    this.currentTimer = null;
    this.emit('stopped');
  }

// ...existing code...

