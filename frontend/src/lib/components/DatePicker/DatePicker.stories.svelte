<script module lang="ts">
  import { defineMeta } from '@storybook/addon-svelte-csf';
  import DatePicker from './DatePicker.svelte';

  // More on how to set up stories at: https://storybook.js.org/docs/writing-stories
  const { Story } = defineMeta({
    title: 'lib/DatePicker',
    component: DatePicker,
    tags: ['autodocs'],
    argTypes: {
      selectedDate: { control: 'date' },
      onSelectDate: { action: 'date selected' },
      class: { control: 'text' }
    }
  });
</script>

<script lang="ts">
  let selectedDate: Date | null = $state(null);

  const handleSelectDate = (date: Date | null) => {
    console.log('Selected date:', date);
  };
</script>

<!-- More on writing stories with args: https://storybook.js.org/docs/writing-stories/args -->
<Story name="DatePicker" args={{ selectedDate, onSelectDate: handleSelectDate }} />

<Story name="After Today" args={{ selectedDate, onSelectDate: handleSelectDate, minDate: new Date() }} />

<Story name="Before Today" args={{ selectedDate, onSelectDate: handleSelectDate, maxDate: new Date() }} />
